package com.cartdetox.config;

import com.cartdetox.model.Product;
import com.cartdetox.model.Reward;
import com.cartdetox.repository.ProductRepository;
import com.cartdetox.repository.RewardRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RewardRepository rewardRepository;

    // Set this in Railway → Variables (UNSPLASH_ACCESS_KEY). Blank = use fallback images only.
    @Value("${UNSPLASH_ACCESS_KEY:}")
    private String unsplashAccessKey;

    // Marker appended to every image URL this code sets, so we only fetch each product once.
    private static final String SEEDED_MARKER = "seeded=v2";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            seedProducts();
        }
        if (rewardRepository.count() == 0) {
            seedRewards();
        }
        ensureImagesAndStock();
    }

    // ---------------------------------------------------------------------
    // IMAGE + STOCK REFRESH
    // ---------------------------------------------------------------------

    private void ensureImagesAndStock() {
        boolean keyConfigured = unsplashAccessKey != null && !unsplashAccessKey.isBlank();

        // Low stock counts — show urgency on clearance + select items
        Map<String, Integer> stock = Map.ofEntries(
            Map.entry("Velvet Reverie Dress", 2),
            Map.entry("Cashmere Crop Pullover", 3),
            Map.entry("Linen Mini Skirt", 2),
            Map.entry("Classic Trench Coat", 3),
            Map.entry("Beaded Evening Clutch", 2),
            Map.entry("Crystal Drop Earrings", 4),
            Map.entry("Tortoise Shell Sunglasses", 3),
            Map.entry("Suede Knee-High Boot", 2),
            Map.entry("Jute Wedge Espadrille", 4),
            Map.entry("Rose Quartz Roller Set", 3),
            Map.entry("Kaolin Clay Face Mask", 5),
            Map.entry("Illuminating Setting Powder", 4),
            Map.entry("Rattan Arch Mirror", 2),
            Map.entry("Cashmere Cocoon Coat", 4),
            Map.entry("Garden Party Midi Dress", 5),
            Map.entry("Bamboo Handle Bag", 3)
        );

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            // Apply urgency stock counts.
            Integer count = stock.get(p.getName());
            if (count != null) p.setStockCount(count);

            // Only fetch an image if this product doesn't already have one we set.
            String current = p.getImageUrl();
            boolean alreadyDone = current != null && current.contains(SEEDED_MARKER);
            if (alreadyDone) continue;

            String query = buildQuery(p);
            String url = keyConfigured ? fetchUnsplash(query) : null;

            if (url != null) {
                // Unsplash hit — premium photo, mark as done so we never re-spend quota on it.
                p.setImageUrl(withMarker(url));
            } else {
                // Fallback: keyword image that always loads + matches the category.
                String fallback = fallbackUrl(p);
                if (keyConfigured) {
                    // Key exists but this fetch failed (e.g. hourly rate limit). Leave it
                    // UNMARKED so a later restart upgrades it to a real Unsplash photo.
                    p.setImageUrl(fallback);
                } else {
                    // No key at all — fallback is final, mark done so we don't loop.
                    p.setImageUrl(withMarker(fallback));
                }
            }
        }
        productRepository.saveAll(products);
    }

    /** Builds a tight search query from the product's tags so the photo matches. */
    private String buildQuery(Product p) {
        List<String> tags = p.getTags();
        if (tags != null && !tags.isEmpty()) {
            return String.join(" ", tags.subList(0, Math.min(3, tags.size())));
        }
        // Fallback to subcategory/category if a product somehow has no tags.
        if (p.getSubcategory() != null && !p.getSubcategory().isBlank()) return p.getSubcategory();
        return p.getCategory() == null ? "product" : p.getCategory();
    }

    /** Queries the Unsplash Search API and returns a 600x700 portrait image URL, or null on failure. */
    private String fetchUnsplash(String query) {
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String endpoint = "https://api.unsplash.com/search/photos"
                    + "?query=" + encoded
                    + "&per_page=1"
                    + "&orientation=portrait"
                    + "&content_filter=high";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Accept-Version", "v1")
                    .header("Authorization", "Client-ID " + unsplashAccessKey)
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> resp = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                System.out.println("[DataInitializer] Unsplash " + resp.statusCode() + " for query: " + query);
                return null;
            }

            JsonNode results = mapper.readTree(resp.body()).path("results");
            if (!results.isArray() || results.isEmpty()) return null;

            String raw = results.get(0).path("urls").path("raw").asText(null);
            if (raw == null || raw.isBlank()) return null;

            // Strip Unsplash's tracking params (ixid, etc.) so the URL stays short
            // enough for the 255-char image_url column, then size it to 600x700 portrait.
            int q = raw.indexOf('?');
            if (q >= 0) raw = raw.substring(0, q);
            return raw + "?auto=format&fit=crop&w=600&h=700";
        } catch (Exception e) {
            System.out.println("[DataInitializer] Unsplash fetch failed for '" + query + "': " + e.getMessage());
            return null;
        }
    }

    /** Always-available keyword image, stable per product via the lock seed. */
    private String fallbackUrl(Product p) {
        String keyword = (p.getSubcategory() != null && !p.getSubcategory().isBlank())
                ? p.getSubcategory()
                : (p.getCategory() == null ? "fashion" : p.getCategory());
        keyword = keyword.toLowerCase().replaceAll("[^a-z]", "");
        long lock = Math.abs(p.getName().hashCode()) % 100000;
        return "https://loremflickr.com/600/700/" + keyword + "?lock=" + lock;
    }

    private String withMarker(String url) {
        String sep = url.contains("?") ? "&" : "?";
        return url + sep + SEEDED_MARKER;
    }

    // ---------------------------------------------------------------------
    // SEED DATA  (imageUrl values here are placeholders; ensureImagesAndStock overwrites them)
    // ---------------------------------------------------------------------

    private void seedProducts() {
        List<Product> products = List.of(
            // === CLOTHING / DRESSES ===
            Product.builder()
                .name("Sundew Silk Slip Dress").category("Clothing").subcategory("Dresses")
                .price(89.0).description("A dreamy sage-toned silk-blend slip dress with delicate adjustable straps and a bias-cut silhouette that moves like water. Effortless from brunch to dinner.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Sage","Ivory","Blush"))
                .tags(List.of("slip dress","silk","summer","elegant")).rating(4.8).reviewCount(234).build(),

            Product.builder()
                .name("Midnight Wrap Dress").category("Clothing").subcategory("Dresses")
                .price(128.0).description("A sophisticated navy wrap dress with a plunging V-neckline, flutter sleeves, and a self-tie waist. The kind of dress that makes every entrance unforgettable.")
                .sizes(List.of("XS","S","M","L","XL","XXL")).colors(List.of("Navy","Burgundy","Forest"))
                .tags(List.of("wrap dress","evening","date night")).rating(4.7).reviewCount(189).build(),

            Product.builder()
                .name("Garden Party Midi Dress").category("Clothing").subcategory("Dresses")
                .price(155.0).description("An enchanting floral midi dress crafted from lightweight chiffon. With its smocked bodice, puff sleeves, and flowing skirt, this is the dress for golden-hour moments.")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Floral Multi","Lavender","Peach"))
                .tags(List.of("midi dress","floral","chiffon","garden")).rating(4.9).reviewCount(312).build(),

            Product.builder()
                .name("Linen Cloud Dress").category("Clothing").subcategory("Dresses")
                .price(98.0).description("Relaxed and romantic, this 100% linen shirt dress features a relaxed fit, chest pockets, and a midi length. The epitome of effortless European summer style.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Natural Linen","White","Sky Blue"))
                .tags(List.of("linen dress","casual","summer","comfortable")).rating(4.6).reviewCount(156).build(),

            Product.builder()
                .name("Velvet Reverie Dress").category("Clothing").subcategory("Dresses")
                .price(195.0).clearancePrice(145.0).isClearance(true)
                .description("Luxuriously rich velvet in a deep plum hue. An off-shoulder neckline and fitted midi silhouette make this the ultimate cocktail dress for evenings that deserve to be remembered.")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Deep Plum","Midnight Black","Forest Green"))
                .tags(List.of("velvet dress","cocktail","evening","off-shoulder")).rating(4.9).reviewCount(98).build(),

            // === CLOTHING / TOPS ===
            Product.builder()
                .name("Ivory Silk Camisole").category("Clothing").subcategory("Tops")
                .price(72.0).description("Pure silk-charmeuse camisole with delicate lace trim and adjustable spaghetti straps. Layer it or wear it alone — either way, it's everything.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Ivory","Champagne","Black","Blush"))
                .tags(List.of("silk camisole","layering","elegant")).rating(4.7).reviewCount(203).build(),

            Product.builder()
                .name("Linen Off-Shoulder Top").category("Clothing").subcategory("Tops")
                .price(65.0).description("A breezy off-shoulder linen top with an elasticated neckline and relaxed fit. Pair with anything from wide-leg trousers to your favorite cutoffs.")
                .sizes(List.of("XS","S","M","L")).colors(List.of("White","Sand","Terracotta"))
                .tags(List.of("linen top","off-shoulder","summer")).rating(4.5).reviewCount(167).build(),

            Product.builder()
                .name("Breton Stripe Tee").category("Clothing").subcategory("Tops")
                .price(58.0).description("The iconic French sailor stripe, reimagined in the softest organic cotton. A wardrobe staple that never goes out of style — for good reason.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Navy Stripe","Black Stripe","Red Stripe"))
                .tags(List.of("striped shirt","cotton tee","classic")).rating(4.8).reviewCount(445).build(),

            Product.builder()
                .name("Ribbed Knit Tank").category("Clothing").subcategory("Tops")
                .price(48.0).description("A fitted ribbed knit tank with a subtle scoop neck. The kind of foundational piece that makes everything in your wardrobe work harder.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Cream","Black","Camel","Sage"))
                .tags(List.of("knit tank top","ribbed","basics")).rating(4.6).reviewCount(389).build(),

            Product.builder()
                .name("Cashmere Crop Pullover").category("Clothing").subcategory("Tops")
                .price(145.0).clearancePrice(98.0).isClearance(true)
                .description("100% Grade-A cashmere in a relaxed cropped fit. It's the sweater you reach for on every cool morning — impossibly soft, effortlessly chic.")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Oatmeal","Dusty Rose","Sky Blue","Sage"))
                .tags(List.of("cashmere sweater","knit","cozy")).rating(4.9).reviewCount(211).build(),

            // === CLOTHING / BOTTOMS ===
            Product.builder()
                .name("High-Rise Wide Leg Trousers").category("Clothing").subcategory("Bottoms")
                .price(115.0).description("Tailored to perfection with a high waist, wide leg, and invisible side zip. The kind of trousers that make you feel like you have your life entirely together.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Chalk","Chocolate","Black","Cream"))
                .tags(List.of("wide leg trousers","tailored","elevated")).rating(4.7).reviewCount(178).build(),

            Product.builder()
                .name("Vintage Wide Leg Denim").category("Clothing").subcategory("Bottoms")
                .price(98.0).description("A vintage-wash wide leg jean in a flattering high-rise cut. Worn-in enough to look like a thrift find, structured enough to look intentional.")
                .sizes(List.of("24","25","26","27","28","29","30","32")).colors(List.of("Light Wash","Mid Wash","Dark Indigo"))
                .tags(List.of("denim jeans","wide leg","vintage")).rating(4.8).reviewCount(523).build(),

            Product.builder()
                .name("Sage Pleated Midi Skirt").category("Clothing").subcategory("Bottoms")
                .price(92.0).description("Flowing pleated satin midi skirt in the most flattering sage green. With its high waist and bias-cut hem, this skirt is pure movement.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Sage","Champagne","Rose"))
                .tags(List.of("pleated skirt","satin","midi")).rating(4.7).reviewCount(142).build(),

            Product.builder()
                .name("Linen Mini Skirt").category("Clothing").subcategory("Bottoms")
                .price(78.0).clearancePrice(55.0).isClearance(true)
                .description("A lightweight linen mini skirt with a relaxed A-line silhouette and raw hem detail. Easy, breezy, and absolutely perfect for summer.")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Natural","White","Clay"))
                .tags(List.of("mini skirt","linen","summer")).rating(4.5).reviewCount(99).build(),

            // === CLOTHING / OUTERWEAR ===
            Product.builder()
                .name("Cashmere Cocoon Coat").category("Clothing").subcategory("Outerwear")
                .price(285.0).description("A luxurious oversized cocoon coat crafted from a cashmere-blend in warm camel. With its clean lines and single-button closure, this coat makes every outfit look intentional.")
                .sizes(List.of("XS/S","M/L","XL/XXL")).colors(List.of("Camel","Ivory","Charcoal"))
                .tags(List.of("camel coat","cashmere","outerwear")).rating(4.9).reviewCount(87).build(),

            Product.builder()
                .name("Velvet Blazer").category("Clothing").subcategory("Outerwear")
                .price(178.0).description("A rich burgundy velvet blazer with a slim-fit silhouette, notched lapels, and satin lining. Dress it up or wear it with jeans — this blazer does the work for you.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Burgundy","Midnight Blue","Forest Green"))
                .tags(List.of("velvet blazer","jacket","elevated")).rating(4.8).reviewCount(134).build(),

            Product.builder()
                .name("Classic Trench Coat").category("Clothing").subcategory("Outerwear")
                .price(245.0).clearancePrice(185.0).isClearance(true)
                .description("The timeless trench, reimagined in a modern silhouette. A double-breasted closure, storm flaps, and belted waist create a coat that works for every season and every decade.")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Camel","Black","Cream"))
                .tags(List.of("trench coat","classic","outerwear")).rating(4.9).reviewCount(256).build(),

            // === ACCESSORIES / BAGS ===
            Product.builder()
                .name("Structured Woven Tote").category("Accessories").subcategory("Bags")
                .price(158.0).description("A sophisticated structured tote handwoven from Italian leather. Spacious enough for your daily essentials with an interior zip pocket to keep you organised.")
                .colors(List.of("Camel","Ivory","Black")).tags(List.of("leather tote bag","structured","everyday")).rating(4.7).reviewCount(198).build(),

            Product.builder()
                .name("Mini Crescent Bag").category("Accessories").subcategory("Bags")
                .price(128.0).description("A sculptural mini crescent bag in buttery soft leather with a sleek top handle and optional crossbody strap. Small but perfectly formed.")
                .colors(List.of("Blush","Cognac","Black","Ivory")).tags(List.of("crossbody bag","mini purse","leather")).rating(4.8).reviewCount(287).build(),

            Product.builder()
                .name("Slouchy Hobo Bag").category("Accessories").subcategory("Bags")
                .price(195.0).description("A relaxed hobo silhouette in the most gorgeous cognac leather. With a spacious interior and adjustable shoulder strap, this bag fits into every lifestyle.")
                .colors(List.of("Cognac","Mushroom","Black")).tags(List.of("hobo handbag","leather bag","everyday")).rating(4.6).reviewCount(143).build(),

            Product.builder()
                .name("Beaded Evening Clutch").category("Accessories").subcategory("Bags")
                .price(88.0).clearancePrice(65.0).isClearance(true)
                .description("A hand-beaded evening clutch that catches the light beautifully. With its gold chain strap and satin interior, this clutch transforms any outfit into an occasion.")
                .colors(List.of("Gold","Silver","Pearl White")).tags(List.of("beaded clutch","evening bag","formal")).rating(4.7).reviewCount(76).build(),

            Product.builder()
                .name("Bamboo Handle Bag").category("Accessories").subcategory("Bags")
                .price(112.0).description("A sculptural bag with bamboo ring handles and a crisp ivory canvas body trimmed in tan leather. Effortlessly chic vacation energy year-round.")
                .colors(List.of("Ivory/Tan","Black/Natural")).tags(List.of("bamboo handbag","canvas bag","summer")).rating(4.5).reviewCount(112).build(),

            // === ACCESSORIES / JEWELRY ===
            Product.builder()
                .name("Oversized Gold Hoops").category("Accessories").subcategory("Jewelry")
                .price(48.0).description("Statement-making 18K gold-plated hoop earrings in a generous size that frames the face beautifully. The one earring that goes with everything.")
                .tags(List.of("gold hoop earrings","jewelry","statement")).rating(4.8).reviewCount(567).build(),

            Product.builder()
                .name("Pearl Pendant Necklace").category("Accessories").subcategory("Jewelry")
                .price(68.0).description("A single freshwater pearl suspended from a delicate 18K gold-plated chain. Minimal, meaningful, and endlessly wearable.")
                .tags(List.of("pearl necklace","pendant","jewelry")).rating(4.9).reviewCount(389).build(),

            Product.builder()
                .name("Stacking Rings Set").category("Accessories").subcategory("Jewelry")
                .price(85.0).description("A curated set of five delicate gold rings — twisted bands, thin stackers, and one with a tiny gemstone detail. Mix, match, stack as you please.")
                .tags(List.of("gold rings","stacking jewelry","set")).rating(4.7).reviewCount(234).build(),

            Product.builder()
                .name("Gold Charm Bracelet").category("Accessories").subcategory("Jewelry")
                .price(95.0).description("A dainty gold chain bracelet adorned with five hand-selected charms — a crescent moon, butterfly, star, heart, and flower. Wear alone or layer generously.")
                .tags(List.of("charm bracelet","gold jewelry","dainty")).rating(4.8).reviewCount(178).build(),

            Product.builder()
                .name("Crystal Drop Earrings").category("Accessories").subcategory("Jewelry")
                .price(72.0).clearancePrice(52.0).isClearance(true)
                .description("Elongated crystal drop earrings that catch the light with every movement. A touch of vintage glamour for the modern woman.")
                .tags(List.of("crystal earrings","drop earrings","glamour")).rating(4.6).reviewCount(123).build(),

            Product.builder()
                .name("Layered Chain Necklace").category("Accessories").subcategory("Jewelry")
                .price(58.0).description("Three delicate 18K gold-plated chains designed to be worn together or separately. The pre-layered look, already perfected for you.")
                .tags(List.of("layered necklace","gold chain","jewelry")).rating(4.7).reviewCount(301).build(),

            // === ACCESSORIES / OTHER ===
            Product.builder()
                .name("Vintage Print Silk Scarf").category("Accessories").subcategory("Scarves")
                .price(78.0).description("A 90cm square silk scarf featuring an original vintage botanical print. Wear it in your hair, around your neck, or tied to your bag handle.")
                .tags(List.of("silk scarf","vintage print","accessory")).rating(4.8).reviewCount(167).build(),

            Product.builder()
                .name("Tortoise Shell Sunglasses").category("Accessories").subcategory("Eyewear")
                .price(88.0).clearancePrice(68.0).isClearance(true)
                .description("Oversized tortoise-shell acetate frames with UV400 lenses. The kind of sunglasses that make every day feel like you're in a film.")
                .tags(List.of("sunglasses","tortoise shell","oversized")).rating(4.7).reviewCount(289).build(),

            // === SHOES ===
            Product.builder()
                .name("Square Toe Block Heel").category("Shoes").subcategory("Heels")
                .price(148.0).description("A sculptural block heel mule with a squared toe, minimal straps, and a comfortable 3-inch heel. The heel that looks high fashion but feels like a walk in the park.")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Camel","Black","Ivory","Nude"))
                .tags(List.of("block heel shoes","mule","heels")).rating(4.6).reviewCount(187).build(),

            Product.builder()
                .name("Strappy Kitten Heel").category("Shoes").subcategory("Heels")
                .price(165.0).description("A barely-there strappy sandal with a delicate kitten heel. Crafted from soft Italian leather with adjustable ankle strap — the definition of understated elegance.")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Gold","Silver","Nude","Black"))
                .tags(List.of("strappy heel sandal","kitten heel","shoes")).rating(4.8).reviewCount(143).build(),

            Product.builder()
                .name("Platform Ankle Boot").category("Shoes").subcategory("Boots")
                .price(215.0).description("A chunky platform ankle boot in glossy leather with an inside zip. The boot that gives every outfit a cool, intentional edge.")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","White","Tan"))
                .tags(List.of("ankle boots","platform","leather")).rating(4.7).reviewCount(198).build(),

            Product.builder()
                .name("Suede Knee-High Boot").category("Shoes").subcategory("Boots")
                .price(285.0).clearancePrice(218.0).isClearance(true)
                .description("Slouchy suede knee-high boots with a low block heel. In a luxurious mocha suede, these boots are the autumnal dream every wardrobe deserves.")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Mocha","Cognac","Black"))
                .tags(List.of("knee high boots","suede","autumn")).rating(4.9).reviewCount(89).build(),

            Product.builder()
                .name("Leather Chelsea Boot").category("Shoes").subcategory("Boots")
                .price(198.0).description("The quintessential Chelsea boot in polished grain leather with elastic gussets and stacked heel. A forever boot that only looks better with age.")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","Tan","Oxblood"))
                .tags(List.of("chelsea boots","leather","classic")).rating(4.8).reviewCount(312).build(),

            Product.builder()
                .name("Platform White Sneaker").category("Shoes").subcategory("Casual")
                .price(125.0).description("A 90s-inspired platform sneaker in pristine white leather. Chunky, cool, and surprisingly versatile — pairs with everything from mini dresses to tailored trousers.")
                .sizes(List.of("35","36","37","38","39","40","41","42")).colors(List.of("White","Cream","Triple Black"))
                .tags(List.of("white sneakers","platform","casual shoes")).rating(4.7).reviewCount(456).build(),

            Product.builder()
                .name("Pointed Ballet Flat").category("Shoes").subcategory("Casual")
                .price(98.0).description("The Parisian ballet flat in supple leather with a delicate pointed toe. Slip on, go anywhere — the shoe that makes every outfit look like it was planned.")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","Ballet Pink","Camel","Navy"))
                .tags(List.of("ballet flats","pointed shoes","classic")).rating(4.8).reviewCount(378).build(),

            Product.builder()
                .name("Jute Wedge Espadrille").category("Shoes").subcategory("Casual")
                .price(88.0).clearancePrice(65.0).isClearance(true)
                .description("A classic espadrille wedge with a natural jute sole and canvas upper. The summer shoe that feels like sunshine bottled into footwear.")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Natural","Navy Stripe","Terracotta"))
                .tags(List.of("espadrille wedge","summer shoes","sandals")).rating(4.6).reviewCount(134).build(),

            // === LIFESTYLE / CANDLES ===
            Product.builder()
                .name("Amber & Vanilla Candle").category("Lifestyle").subcategory("Candles")
                .price(38.0).description("Hand-poured in small batches using a coconut-soy wax blend. Notes of warm amber, spiced vanilla, and sandalwood create a cocooning, golden-hour atmosphere.")
                .tags(List.of("scented candle","amber","cozy")).rating(4.9).reviewCount(445).build(),

            Product.builder()
                .name("Forest Walk Candle").category("Lifestyle").subcategory("Candles")
                .price(42.0).description("An evocative candle that captures the feeling of morning light through a pine forest. Notes of cedarwood, moss, and bergamot. 60-hour burn time.")
                .tags(List.of("scented candle","cedar","home")).rating(4.8).reviewCount(312).build(),

            // === LIFESTYLE / HOME ===
            Product.builder()
                .name("Washed Linen Throw").category("Lifestyle").subcategory("Home")
                .price(85.0).description("A generously-sized throw in stone-washed linen. Impossibly soft, perfectly rumpled, and looks beautiful draped over literally anything.")
                .tags(List.of("linen throw blanket","home decor","cozy")).rating(4.7).reviewCount(189).build(),

            Product.builder()
                .name("Rattan Arch Mirror").category("Lifestyle").subcategory("Home")
                .price(128.0).clearancePrice(95.0).isClearance(true)
                .description("A floor-to-wall arch mirror framed in hand-woven natural rattan. Makes any space feel larger, warmer, and more curated. The interior designer's secret weapon.")
                .tags(List.of("arch mirror","rattan home decor","mirror")).rating(4.8).reviewCount(98).build(),

            Product.builder()
                .name("Dried Floral Arrangement").category("Lifestyle").subcategory("Home")
                .price(55.0).description("A curated arrangement of dried pampas grass, lunaria, and preserved roses in a vintage terracotta vase. No watering required — everlasting beauty.")
                .tags(List.of("dried flowers","pampas grass","home decor")).rating(4.6).reviewCount(156).build(),

            // === LIFESTYLE / STATIONERY ===
            Product.builder()
                .name("Pressed Flower Journal").category("Lifestyle").subcategory("Stationery")
                .price(32.0).description("A handmade journal with a pressed wildflower cover, 200 pages of thick cream-toned paper, and a ribbon bookmark. Your most beautiful thoughts deserve a beautiful home.")
                .tags(List.of("journal notebook","stationery","writing")).rating(4.9).reviewCount(234).build(),

            Product.builder()
                .name("Ceramic Mug Duo").category("Lifestyle").subcategory("Stationery")
                .price(58.0).description("A set of two hand-thrown matte ceramic mugs in a speckled sage glaze. Because your morning ritual deserves vessels that make you smile before the first sip.")
                .tags(List.of("ceramic mug","coffee cup","kitchen")).rating(4.8).reviewCount(312).build(),

            // === LIFESTYLE / WELLNESS ===
            Product.builder()
                .name("Natural Rubber Yoga Mat").category("Lifestyle").subcategory("Wellness")
                .price(88.0).description("A 4mm natural rubber yoga mat with an artisanal tie-dye print in sage and ivory. Non-slip, eco-friendly, and beautiful enough to leave out as a decorative piece.")
                .tags(List.of("yoga mat","wellness","fitness")).rating(4.7).reviewCount(178).build(),

            Product.builder()
                .name("Rose Quartz Roller Set").category("Lifestyle").subcategory("Wellness")
                .price(65.0).clearancePrice(48.0).isClearance(true)
                .description("A curated facial massage set with a genuine rose quartz roller and carved gua sha tool. Depuff, sculpt, and take five minutes that are entirely yours.")
                .tags(List.of("jade roller gua sha","rose quartz","skincare tool")).rating(4.8).reviewCount(456).build(),

            // === BEAUTY / SKINCARE ===
            Product.builder()
                .name("Hyaluronic Glow Serum").category("Beauty").subcategory("Skincare")
                .price(72.0).description("A lightweight serum with triple-weight hyaluronic acid and vitamin B5 that plumps, hydrates, and gives your skin a lit-from-within glow. 30ml, 60-day supply.")
                .tags(List.of("face serum","skincare bottle","glow")).rating(4.9).reviewCount(567).build(),

            Product.builder()
                .name("Botanical Rose Toner").category("Beauty").subcategory("Skincare")
                .price(45.0).description("An alcohol-free facial toner with Bulgarian rose water, niacinamide, and green tea extract. Balances, brightens, and makes your skin feel like it just had a vacation.")
                .tags(List.of("facial toner","skincare","rose")).rating(4.8).reviewCount(389).build(),

            Product.builder()
                .name("Kaolin Clay Face Mask").category("Beauty").subcategory("Skincare")
                .price(38.0).clearancePrice(28.0).isClearance(true)
                .description("A gentle kaolin clay mask enriched with oat extract and chamomile. Draws out impurities while calming irritation — the Sunday ritual your skin has been asking for.")
                .tags(List.of("clay face mask","skincare jar","beauty")).rating(4.7).reviewCount(234).build(),

            Product.builder()
                .name("Vitamin C Brightening Cream").category("Beauty").subcategory("Skincare")
                .price(68.0).description("A rich yet fast-absorbing day cream with stabilized Vitamin C, bakuchiol, and shea butter. Visibly evens skin tone and protects against environmental stressors.")
                .tags(List.of("face cream jar","moisturizer","skincare")).rating(4.8).reviewCount(312).build(),

            // === BEAUTY / FRAGRANCE ===
            Product.builder()
                .name("Golden Hour Eau de Parfum").category("Beauty").subcategory("Fragrance")
                .price(95.0).description("A warm, enveloping fragrance that opens with bergamot and orange blossom before settling into a heart of jasmine, amber, and golden musk. 50ml.")
                .tags(List.of("perfume bottle","fragrance","luxury")).rating(4.9).reviewCount(198).build(),

            Product.builder()
                .name("Garden Notes Perfume").category("Beauty").subcategory("Fragrance")
                .price(88.0).description("A fresh, green floral that captures the feeling of wandering through a rain-kissed garden. Notes of violet leaf, peony, lily of the valley, and white cedar. 50ml.")
                .tags(List.of("perfume bottle","floral fragrance","beauty")).rating(4.8).reviewCount(156).build(),

            // === BEAUTY / MAKEUP ===
            Product.builder()
                .name("Tinted Lip Treatment").category("Beauty").subcategory("Makeup")
                .price(22.0).description("A nourishing lip balm-gloss hybrid tinted with the most perfect natural rose. SPF 30, enriched with jojoba oil and shea butter. The one product you'll always reach for.")
                .tags(List.of("lip gloss balm","tinted lip","makeup")).rating(4.9).reviewCount(678).build(),

            Product.builder()
                .name("Illuminating Setting Powder").category("Beauty").subcategory("Makeup")
                .price(42.0).clearancePrice(32.0).isClearance(true)
                .description("A finely-milled translucent setting powder with a subtle luminosity that photographs beautifully. Sets makeup for 16 hours while giving skin an airbrushed, lit-from-within finish.")
                .tags(List.of("setting powder compact","makeup","beauty")).rating(4.7).reviewCount(245).build()
        );

        productRepository.saveAll(products);
    }

    private void seedRewards() {
        List<Reward> rewards = List.of(
            Reward.builder().title("Mindful Morning Walk").icon("🌿")
                .description("Take a 20-minute walk outside without your phone. Notice five things you see, hear, and feel.")
                .tokenReward(75).category("Movement").isRepeatable(true).build(),

            Reward.builder().title("Closet Clarity").icon("👚")
                .description("Identify 5 items in your wardrobe you haven't worn in 6+ months. Bag them for donation.")
                .tokenReward(150).category("Declutter").isRepeatable(false).build(),

            Reward.builder().title("Shopping Journal").icon("📓")
                .description("Write for 10 minutes about what you were feeling the last time you felt the urge to shop.")
                .tokenReward(50).category("Mindfulness").isRepeatable(true).build(),

            Reward.builder().title("Screen-Free Hour").icon("📵")
                .description("Spend one full hour completely offline. Read, cook, draw, stretch — anything but a screen.")
                .tokenReward(100).category("Mindfulness").isRepeatable(true).build(),

            Reward.builder().title("Donation Drive").icon("🎁")
                .description("Donate items to a local charity shop or shelter. Share what you gave with someone you love.")
                .tokenReward(200).category("Declutter").isRepeatable(true).build(),

            Reward.builder().title("Try Something New").icon("✨")
                .description("Spend one hour trying a new hobby — sketching, cooking, knitting, whatever calls to you.")
                .tokenReward(75).category("Creativity").isRepeatable(true).build(),

            Reward.builder().title("24-Hour Browse Detox").icon("🛑")
                .description("Go a full 24 hours without opening any shopping apps or websites. You can do this.")
                .tokenReward(200).category("Mindfulness").isRepeatable(true).build(),

            Reward.builder().title("Gratitude List").icon("💛")
                .description("Write down 10 things you already own that bring you genuine joy. Sit with that feeling.")
                .tokenReward(50).category("Mindfulness").isRepeatable(true).build(),

            Reward.builder().title("Style What You Have").icon("👗")
                .description("Create 3 new outfits entirely from clothes already in your closet. Photograph your favourites.")
                .tokenReward(100).category("Creativity").isRepeatable(true).build(),

            Reward.builder().title("Meditation Session").icon("🧘")
                .description("Complete a 15-minute meditation session. Focus on what truly fulfils you beyond material things.")
                .tokenReward(75).category("Mindfulness").isRepeatable(true).build(),

            Reward.builder().title("Repair Instead of Replace").icon("🪡")
                .description("Repair or mend something you own instead of replacing it. A button, a hem, a scuff — it counts.")
                .tokenReward(125).category("Sustainability").isRepeatable(true).build(),

            Reward.builder().title("Swap With a Friend").icon("🤝")
                .description("Organise a clothing swap with at least one friend. Exchange instead of buying new.")
                .tokenReward(175).category("Community").isRepeatable(true).build()
        );

        rewardRepository.saveAll(rewards);
    }
}