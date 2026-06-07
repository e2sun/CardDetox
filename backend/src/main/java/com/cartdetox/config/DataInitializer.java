package com.cartdetox.config;

import com.cartdetox.model.Product;
import com.cartdetox.model.Reward;
import com.cartdetox.repository.ProductRepository;
import com.cartdetox.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RewardRepository rewardRepository;

    private static final String IMG = "https://images.unsplash.com/photo-";

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            seedProducts();
        }
        if (rewardRepository.count() == 0) {
            seedRewards();
        }
    }

    private void seedProducts() {
        List<Product> products = List.of(
            // === CLOTHING / DRESSES ===
            Product.builder()
                .name("Sundew Silk Slip Dress").category("Clothing").subcategory("Dresses")
                .price(89.0).description("A dreamy sage-toned silk-blend slip dress with delicate adjustable straps and a bias-cut silhouette that moves like water. Effortless from brunch to dinner.")
                .imageUrl(IMG + "1515886657613-9f3515b0c78f?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Sage","Ivory","Blush"))
                .tags(List.of("slip dress","silk","summer","elegant")).rating(4.8).reviewCount(234).build(),

            Product.builder()
                .name("Midnight Wrap Dress").category("Clothing").subcategory("Dresses")
                .price(128.0).description("A sophisticated navy wrap dress with a plunging V-neckline, flutter sleeves, and a self-tie waist. The kind of dress that makes every entrance unforgettable.")
                .imageUrl(IMG + "1496747611176-843222e1e57c?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL","XXL")).colors(List.of("Navy","Burgundy","Forest"))
                .tags(List.of("wrap dress","evening","date night")).rating(4.7).reviewCount(189).build(),

            Product.builder()
                .name("Garden Party Midi Dress").category("Clothing").subcategory("Dresses")
                .price(155.0).description("An enchanting floral midi dress crafted from lightweight chiffon. With its smocked bodice, puff sleeves, and flowing skirt, this is the dress for golden-hour moments.")
                .imageUrl(IMG + "1490481651871-ab68de25d43d?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Floral Multi","Lavender","Peach"))
                .tags(List.of("midi dress","floral","chiffon","garden")).rating(4.9).reviewCount(312).build(),

            Product.builder()
                .name("Linen Cloud Dress").category("Clothing").subcategory("Dresses")
                .price(98.0).description("Relaxed and romantic, this 100% linen shirt dress features a relaxed fit, chest pockets, and a midi length. The epitome of effortless European summer style.")
                .imageUrl(IMG + "1469334031218-e382a71b716b?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Natural Linen","White","Sky Blue"))
                .tags(List.of("linen","casual","summer","comfortable")).rating(4.6).reviewCount(156).build(),

            Product.builder()
                .name("Velvet Reverie Dress").category("Clothing").subcategory("Dresses")
                .price(195.0).clearancePrice(145.0).isClearance(true)
                .description("Luxuriously rich velvet in a deep plum hue. An off-shoulder neckline and fitted midi silhouette make this the ultimate cocktail dress for evenings that deserve to be remembered.")
                .imageUrl(IMG + "1485968579580-b6d095142e6e?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Deep Plum","Midnight Black","Forest Green"))
                .tags(List.of("velvet","cocktail","evening","off-shoulder")).rating(4.9).reviewCount(98).build(),

            // === CLOTHING / TOPS ===
            Product.builder()
                .name("Ivory Silk Camisole").category("Clothing").subcategory("Tops")
                .price(72.0).description("Pure silk-charmeuse camisole with delicate lace trim and adjustable spaghetti straps. Layer it or wear it alone — either way, it's everything.")
                .imageUrl(IMG + "1509631179647-0177331693ae?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Ivory","Champagne","Black","Blush"))
                .tags(List.of("silk","camisole","layering","elegant")).rating(4.7).reviewCount(203).build(),

            Product.builder()
                .name("Linen Off-Shoulder Top").category("Clothing").subcategory("Tops")
                .price(65.0).description("A breezy off-shoulder linen top with an elasticated neckline and relaxed fit. Pair with anything from wide-leg trousers to your favorite cutoffs.")
                .imageUrl(IMG + "1434389677669-e08b4cac3105?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L")).colors(List.of("White","Sand","Terracotta"))
                .tags(List.of("linen","off-shoulder","summer","casual")).rating(4.5).reviewCount(167).build(),

            Product.builder()
                .name("Breton Stripe Tee").category("Clothing").subcategory("Tops")
                .price(58.0).description("The iconic French sailor stripe, reimagined in the softest organic cotton. A wardrobe staple that never goes out of style — for good reason.")
                .imageUrl(IMG + "1484327369686-b5b65e4ff9c2?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Navy Stripe","Black Stripe","Red Stripe"))
                .tags(List.of("stripe","cotton","classic","French")).rating(4.8).reviewCount(445).build(),

            Product.builder()
                .name("Ribbed Knit Tank").category("Clothing").subcategory("Tops")
                .price(48.0).description("A fitted ribbed knit tank with a subtle scoop neck. The kind of foundational piece that makes everything in your wardrobe work harder.")
                .imageUrl(IMG + "1506152983158-b4a74a01c721?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Cream","Black","Camel","Sage"))
                .tags(List.of("ribbed","knit","layering","basics")).rating(4.6).reviewCount(389).build(),

            Product.builder()
                .name("Cashmere Crop Pullover").category("Clothing").subcategory("Tops")
                .price(145.0).clearancePrice(98.0).isClearance(true)
                .description("100% Grade-A cashmere in a relaxed cropped fit. It's the sweater you reach for on every cool morning — impossibly soft, effortlessly chic.")
                .imageUrl(IMG + "1576566588028-4147f3842f27?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Oatmeal","Dusty Rose","Sky Blue","Sage"))
                .tags(List.of("cashmere","sweater","luxury","cozy")).rating(4.9).reviewCount(211).build(),

            // === CLOTHING / BOTTOMS ===
            Product.builder()
                .name("High-Rise Wide Leg Trousers").category("Clothing").subcategory("Bottoms")
                .price(115.0).description("Tailored to perfection with a high waist, wide leg, and invisible side zip. The kind of trousers that make you feel like you have your life entirely together.")
                .imageUrl(IMG + "1509551388413-e18d0ac5d495?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Chalk","Chocolate","Black","Cream"))
                .tags(List.of("wide leg","tailored","trousers","elevated")).rating(4.7).reviewCount(178).build(),

            Product.builder()
                .name("Vintage Wide Leg Denim").category("Clothing").subcategory("Bottoms")
                .price(98.0).description("A vintage-wash wide leg jean in a flattering high-rise cut. Worn-in enough to look like a thrift find, structured enough to look intentional.")
                .imageUrl(IMG + "1541099649105-f69ad21f3246?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("24","25","26","27","28","29","30","32")).colors(List.of("Light Wash","Mid Wash","Dark Indigo"))
                .tags(List.of("denim","wide leg","vintage","jeans")).rating(4.8).reviewCount(523).build(),

            Product.builder()
                .name("Sage Pleated Midi Skirt").category("Clothing").subcategory("Bottoms")
                .price(92.0).description("Flowing pleated satin midi skirt in the most flattering sage green. With its high waist and bias-cut hem, this skirt is pure movement.")
                .imageUrl(IMG + "1618354691792-d1d42acfd860?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Sage","Champagne","Rose"))
                .tags(List.of("pleated","midi","satin","skirt")).rating(4.7).reviewCount(142).build(),

            Product.builder()
                .name("Linen Mini Skirt").category("Clothing").subcategory("Bottoms")
                .price(78.0).clearancePrice(55.0).isClearance(true)
                .description("A lightweight linen mini skirt with a relaxed A-line silhouette and raw hem detail. Easy, breezy, and absolutely perfect for summer.")
                .imageUrl(IMG + "1594938298603-f8b2f0f6c2af?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L")).colors(List.of("Natural","White","Clay"))
                .tags(List.of("linen","mini","summer","casual")).rating(4.5).reviewCount(99).build(),

            // === CLOTHING / OUTERWEAR ===
            Product.builder()
                .name("Cashmere Cocoon Coat").category("Clothing").subcategory("Outerwear")
                .price(285.0).description("A luxurious oversized cocoon coat crafted from a cashmere-blend in warm camel. With its clean lines and single-button closure, this coat makes every outfit look intentional.")
                .imageUrl(IMG + "1539533018447-50cce9a83e61?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS/S","M/L","XL/XXL")).colors(List.of("Camel","Ivory","Charcoal"))
                .tags(List.of("coat","cashmere","outerwear","luxury")).rating(4.9).reviewCount(87).build(),

            Product.builder()
                .name("Velvet Blazer").category("Clothing").subcategory("Outerwear")
                .price(178.0).description("A rich burgundy velvet blazer with a slim-fit silhouette, notched lapels, and satin lining. Dress it up or wear it with jeans — this blazer does the work for you.")
                .imageUrl(IMG + "1552902865-b72c031ac5ea?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Burgundy","Midnight Blue","Forest Green"))
                .tags(List.of("blazer","velvet","statement","elevated")).rating(4.8).reviewCount(134).build(),

            Product.builder()
                .name("Classic Trench Coat").category("Clothing").subcategory("Outerwear")
                .price(245.0).clearancePrice(185.0).isClearance(true)
                .description("The timeless trench, reimagined in a modern silhouette. A double-breasted closure, storm flaps, and belted waist create a coat that works for every season and every decade.")
                .imageUrl(IMG + "1488161628813-04466f872be2?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("XS","S","M","L","XL")).colors(List.of("Camel","Black","Cream"))
                .tags(List.of("trench","classic","outerwear","timeless")).rating(4.9).reviewCount(256).build(),

            // === ACCESSORIES / BAGS ===
            Product.builder()
                .name("Structured Woven Tote").category("Accessories").subcategory("Bags")
                .price(158.0).description("A sophisticated structured tote handwoven from Italian leather. Spacious enough for your daily essentials with an interior zip pocket to keep you organised.")
                .imageUrl(IMG + "1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&h=700")
                .colors(List.of("Camel","Ivory","Black")).tags(List.of("tote","leather","structured","everyday")).rating(4.7).reviewCount(198).build(),

            Product.builder()
                .name("Mini Crescent Bag").category("Accessories").subcategory("Bags")
                .price(128.0).description("A sculptural mini crescent bag in buttery soft leather with a sleek top handle and optional crossbody strap. Small but perfectly formed.")
                .imageUrl(IMG + "1548036328-c9fa89d128fa?auto=format&fit=crop&w=600&h=700")
                .colors(List.of("Blush","Cognac","Black","Ivory")).tags(List.of("mini","crescent","crossbody","cute")).rating(4.8).reviewCount(287).build(),

            Product.builder()
                .name("Slouchy Hobo Bag").category("Accessories").subcategory("Bags")
                .price(195.0).description("A relaxed hobo silhouette in the most gorgeous cognac leather. With a spacious interior and adjustable shoulder strap, this bag fits into every lifestyle.")
                .imageUrl(IMG + "1590874103328-eac38a683ce7?auto=format&fit=crop&w=600&h=700")
                .colors(List.of("Cognac","Mushroom","Black")).tags(List.of("hobo","slouchy","leather","everyday")).rating(4.6).reviewCount(143).build(),

            Product.builder()
                .name("Beaded Evening Clutch").category("Accessories").subcategory("Bags")
                .price(88.0).clearancePrice(65.0).isClearance(true)
                .description("A hand-beaded evening clutch that catches the light beautifully. With its gold chain strap and satin interior, this clutch transforms any outfit into an occasion.")
                .imageUrl(IMG + "1566150905458-1bf1fc113f0d?auto=format&fit=crop&w=600&h=700")
                .colors(List.of("Gold","Silver","Pearl White")).tags(List.of("clutch","evening","beaded","formal")).rating(4.7).reviewCount(76).build(),

            Product.builder()
                .name("Bamboo Handle Bag").category("Accessories").subcategory("Bags")
                .price(112.0).description("A sculptural bag with bamboo ring handles and a crisp ivory canvas body trimmed in tan leather. Effortlessly chic vacation energy year-round.")
                .imageUrl(IMG + "1584917865442-de89df76afd3?auto=format&fit=crop&w=600&h=700")
                .colors(List.of("Ivory/Tan","Black/Natural")).tags(List.of("bamboo","canvas","summer","vacation")).rating(4.5).reviewCount(112).build(),

            // === ACCESSORIES / JEWELRY ===
            Product.builder()
                .name("Oversized Gold Hoops").category("Accessories").subcategory("Jewelry")
                .price(48.0).description("Statement-making 18K gold-plated hoop earrings in a generous size that frames the face beautifully. The one earring that goes with everything.")
                .imageUrl(IMG + "1506630268652-29e6f32e9e04?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("earrings","gold","hoops","statement")).rating(4.8).reviewCount(567).build(),

            Product.builder()
                .name("Pearl Pendant Necklace").category("Accessories").subcategory("Jewelry")
                .price(68.0).description("A single freshwater pearl suspended from a delicate 18K gold-plated chain. Minimal, meaningful, and endlessly wearable.")
                .imageUrl(IMG + "1515562141207-7a88fb7ce338?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("necklace","pearl","minimal","elegant")).rating(4.9).reviewCount(389).build(),

            Product.builder()
                .name("Stacking Rings Set").category("Accessories").subcategory("Jewelry")
                .price(85.0).description("A curated set of five delicate gold rings — twisted bands, thin stackers, and one with a tiny gemstone detail. Mix, match, stack as you please.")
                .imageUrl(IMG + "1611085583191-a3b181a88401?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("rings","stacking","gold","set")).rating(4.7).reviewCount(234).build(),

            Product.builder()
                .name("Gold Charm Bracelet").category("Accessories").subcategory("Jewelry")
                .price(95.0).description("A dainty gold chain bracelet adorned with five hand-selected charms — a crescent moon, butterfly, star, heart, and flower. Wear alone or layer generously.")
                .imageUrl(IMG + "1594938538-bac1e5a4a52d?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("bracelet","charm","gold","layering")).rating(4.8).reviewCount(178).build(),

            Product.builder()
                .name("Crystal Drop Earrings").category("Accessories").subcategory("Jewelry")
                .price(72.0).clearancePrice(52.0).isClearance(true)
                .description("Elongated crystal drop earrings that catch the light with every movement. A touch of vintage glamour for the modern woman.")
                .imageUrl(IMG + "1583743814966-84149a3f1f25?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("earrings","crystal","drop","glamour")).rating(4.6).reviewCount(123).build(),

            Product.builder()
                .name("Layered Chain Necklace").category("Accessories").subcategory("Jewelry")
                .price(58.0).description("Three delicate 18K gold-plated chains designed to be worn together or separately. The pre-layered look, already perfected for you.")
                .imageUrl(IMG + "1576868872538-16a32c0c6e4a?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("necklace","layered","chain","gold")).rating(4.7).reviewCount(301).build(),

            // === ACCESSORIES / OTHER ===
            Product.builder()
                .name("Vintage Print Silk Scarf").category("Accessories").subcategory("Scarves")
                .price(78.0).description("A 90cm square silk scarf featuring an original vintage botanical print. Wear it in your hair, around your neck, or tied to your bag handle.")
                .imageUrl(IMG + "1558618666-fcd25c85cd64?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("scarf","silk","vintage","versatile")).rating(4.8).reviewCount(167).build(),

            Product.builder()
                .name("Tortoise Shell Sunglasses").category("Accessories").subcategory("Eyewear")
                .price(88.0).clearancePrice(68.0).isClearance(true)
                .description("Oversized tortoise-shell acetate frames with UV400 lenses. The kind of sunglasses that make every day feel like you're in a film.")
                .imageUrl(IMG + "1553361371-9b22f78e8b1d?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("sunglasses","tortoise","oversized","summer")).rating(4.7).reviewCount(289).build(),

            // === SHOES ===
            Product.builder()
                .name("Square Toe Block Heel").category("Shoes").subcategory("Heels")
                .price(148.0).description("A sculptural block heel mule with a squared toe, minimal straps, and a comfortable 3-inch heel. The heel that looks high fashion but feels like a walk in the park.")
                .imageUrl(IMG + "1543163521-1bf539c55dd2?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Camel","Black","Ivory","Nude"))
                .tags(List.of("heels","block heel","mule","elevated")).rating(4.6).reviewCount(187).build(),

            Product.builder()
                .name("Strappy Kitten Heel").category("Shoes").subcategory("Heels")
                .price(165.0).description("A barely-there strappy sandal with a delicate kitten heel. Crafted from soft Italian leather with adjustable ankle strap — the definition of understated elegance.")
                .imageUrl(IMG + "1554284126-aa88f22d8b74?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Gold","Silver","Nude","Black"))
                .tags(List.of("sandal","kitten heel","strappy","evening")).rating(4.8).reviewCount(143).build(),

            Product.builder()
                .name("Platform Ankle Boot").category("Shoes").subcategory("Boots")
                .price(215.0).description("A chunky platform ankle boot in glossy leather with an inside zip. The boot that gives every outfit a cool, intentional edge.")
                .imageUrl(IMG + "1603487742131-4160ec999306?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","White","Tan"))
                .tags(List.of("boots","platform","ankle","edgy")).rating(4.7).reviewCount(198).build(),

            Product.builder()
                .name("Suede Knee-High Boot").category("Shoes").subcategory("Boots")
                .price(285.0).clearancePrice(218.0).isClearance(true)
                .description("Slouchy suede knee-high boots with a low block heel. In a luxurious mocha suede, these boots are the autumnal dream every wardrobe deserves.")
                .imageUrl(IMG + "1528701800489-c560a8e9d0e6?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Mocha","Cognac","Black"))
                .tags(List.of("boots","knee-high","suede","autumn")).rating(4.9).reviewCount(89).build(),

            Product.builder()
                .name("Leather Chelsea Boot").category("Shoes").subcategory("Boots")
                .price(198.0).description("The quintessential Chelsea boot in polished grain leather with elastic gussets and stacked heel. A forever boot that only looks better with age.")
                .imageUrl(IMG + "1588258219511-64eb629cb833?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","Tan","Oxblood"))
                .tags(List.of("chelsea","leather","classic","boots")).rating(4.8).reviewCount(312).build(),

            Product.builder()
                .name("Platform White Sneaker").category("Shoes").subcategory("Casual")
                .price(125.0).description("A 90s-inspired platform sneaker in pristine white leather. Chunky, cool, and surprisingly versatile — pairs with everything from mini dresses to tailored trousers.")
                .imageUrl(IMG + "1542291026-7eec264c27ff?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40","41","42")).colors(List.of("White","Cream","Triple Black"))
                .tags(List.of("sneaker","platform","white","casual")).rating(4.7).reviewCount(456).build(),

            Product.builder()
                .name("Pointed Ballet Flat").category("Shoes").subcategory("Casual")
                .price(98.0).description("The Parisian ballet flat in supple leather with a delicate pointed toe. Slip on, go anywhere — the shoe that makes every outfit look like it was planned.")
                .imageUrl(IMG + "1554220235-e9c99e51afe5?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40","41")).colors(List.of("Black","Ballet Pink","Camel","Navy"))
                .tags(List.of("ballet flat","pointed","minimal","classic")).rating(4.8).reviewCount(378).build(),

            Product.builder()
                .name("Jute Wedge Espadrille").category("Shoes").subcategory("Casual")
                .price(88.0).clearancePrice(65.0).isClearance(true)
                .description("A classic espadrille wedge with a natural jute sole and canvas upper. The summer shoe that feels like sunshine bottled into footwear.")
                .imageUrl(IMG + "1536959769671-37b3c9b1765b?auto=format&fit=crop&w=600&h=700")
                .sizes(List.of("35","36","37","38","39","40")).colors(List.of("Natural","Navy Stripe","Terracotta"))
                .tags(List.of("espadrille","wedge","summer","vacation")).rating(4.6).reviewCount(134).build(),

            // === LIFESTYLE / CANDLES ===
            Product.builder()
                .name("Amber & Vanilla Candle").category("Lifestyle").subcategory("Candles")
                .price(38.0).description("Hand-poured in small batches using a coconut-soy wax blend. Notes of warm amber, spiced vanilla, and sandalwood create a cocooning, golden-hour atmosphere.")
                .imageUrl(IMG + "1608181831718-c9d180d1b42c?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("candle","amber","vanilla","cozy")).rating(4.9).reviewCount(445).build(),

            Product.builder()
                .name("Forest Walk Candle").category("Lifestyle").subcategory("Candles")
                .price(42.0).description("An evocative candle that captures the feeling of morning light through a pine forest. Notes of cedarwood, moss, and bergamot. 60-hour burn time.")
                .imageUrl(IMG + "1602874801006-a7ceb9e4c67e?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("candle","cedar","forest","fresh")).rating(4.8).reviewCount(312).build(),

            // === LIFESTYLE / HOME ===
            Product.builder()
                .name("Washed Linen Throw").category("Lifestyle").subcategory("Home")
                .price(85.0).description("A generously-sized throw in stone-washed linen. Impossibly soft, perfectly rumpled, and looks beautiful draped over literally anything.")
                .imageUrl(IMG + "1578662996442-48f60103fc96?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("throw","linen","home","cozy")).rating(4.7).reviewCount(189).build(),

            Product.builder()
                .name("Rattan Arch Mirror").category("Lifestyle").subcategory("Home")
                .price(128.0).clearancePrice(95.0).isClearance(true)
                .description("A floor-to-wall arch mirror framed in hand-woven natural rattan. Makes any space feel larger, warmer, and more curated. The interior designer's secret weapon.")
                .imageUrl(IMG + "1555041469-a586c61ea9bc?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("mirror","rattan","boho","home decor")).rating(4.8).reviewCount(98).build(),

            Product.builder()
                .name("Dried Floral Arrangement").category("Lifestyle").subcategory("Home")
                .price(55.0).description("A curated arrangement of dried pampas grass, lunaria, and preserved roses in a vintage terracotta vase. No watering required — everlasting beauty.")
                .imageUrl(IMG + "1490750967868-88df5691cc98?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("dried flowers","pampas","home","decor")).rating(4.6).reviewCount(156).build(),

            // === LIFESTYLE / STATIONERY ===
            Product.builder()
                .name("Pressed Flower Journal").category("Lifestyle").subcategory("Stationery")
                .price(32.0).description("A handmade journal with a pressed wildflower cover, 200 pages of thick cream-toned paper, and a ribbon bookmark. Your most beautiful thoughts deserve a beautiful home.")
                .imageUrl(IMG + "1531346878377-a5be20888e57?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("journal","stationery","handmade","writing")).rating(4.9).reviewCount(234).build(),

            Product.builder()
                .name("Ceramic Mug Duo").category("Lifestyle").subcategory("Stationery")
                .price(58.0).description("A set of two hand-thrown matte ceramic mugs in a speckled sage glaze. Because your morning ritual deserves vessels that make you smile before the first sip.")
                .imageUrl(IMG + "1514228742587-6b1558fcca3d?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("mug","ceramic","kitchen","gift")).rating(4.8).reviewCount(312).build(),

            // === LIFESTYLE / WELLNESS ===
            Product.builder()
                .name("Natural Rubber Yoga Mat").category("Lifestyle").subcategory("Wellness")
                .price(88.0).description("A 4mm natural rubber yoga mat with an artisanal tie-dye print in sage and ivory. Non-slip, eco-friendly, and beautiful enough to leave out as a decorative piece.")
                .imageUrl(IMG + "1599901860904-17e6ed7083a0?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("yoga","wellness","mat","eco")).rating(4.7).reviewCount(178).build(),

            Product.builder()
                .name("Rose Quartz Roller Set").category("Lifestyle").subcategory("Wellness")
                .price(65.0).clearancePrice(48.0).isClearance(true)
                .description("A curated facial massage set with a genuine rose quartz roller and carved gua sha tool. Depuff, sculpt, and take five minutes that are entirely yours.")
                .imageUrl(IMG + "1519125323398-675f0ddb6308?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("gua sha","rose quartz","wellness","skincare")).rating(4.8).reviewCount(456).build(),

            // === BEAUTY / SKINCARE ===
            Product.builder()
                .name("Hyaluronic Glow Serum").category("Beauty").subcategory("Skincare")
                .price(72.0).description("A lightweight serum with triple-weight hyaluronic acid and vitamin B5 that plumps, hydrates, and gives your skin a lit-from-within glow. 30ml, 60-day supply.")
                .imageUrl(IMG + "1556228578-8c89e6adf883?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("serum","hyaluronic","glow","skincare")).rating(4.9).reviewCount(567).build(),

            Product.builder()
                .name("Botanical Rose Toner").category("Beauty").subcategory("Skincare")
                .price(45.0).description("An alcohol-free facial toner with Bulgarian rose water, niacinamide, and green tea extract. Balances, brightens, and makes your skin feel like it just had a vacation.")
                .imageUrl(IMG + "1599305445671-ac291c95aaa9?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("toner","rose","brightening","skincare")).rating(4.8).reviewCount(389).build(),

            Product.builder()
                .name("Kaolin Clay Face Mask").category("Beauty").subcategory("Skincare")
                .price(38.0).clearancePrice(28.0).isClearance(true)
                .description("A gentle kaolin clay mask enriched with oat extract and chamomile. Draws out impurities while calming irritation — the Sunday ritual your skin has been asking for.")
                .imageUrl(IMG + "1613440534903-c8f0d3e7a5fe?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("face mask","clay","skincare","clarifying")).rating(4.7).reviewCount(234).build(),

            Product.builder()
                .name("Vitamin C Brightening Cream").category("Beauty").subcategory("Skincare")
                .price(68.0).description("A rich yet fast-absorbing day cream with stabilized Vitamin C, bakuchiol, and shea butter. Visibly evens skin tone and protects against environmental stressors.")
                .imageUrl(IMG + "1620916566398-39f1143ab7be?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("vitamin c","brightening","moisturiser","skincare")).rating(4.8).reviewCount(312).build(),

            // === BEAUTY / FRAGRANCE ===
            Product.builder()
                .name("Golden Hour Eau de Parfum").category("Beauty").subcategory("Fragrance")
                .price(95.0).description("A warm, enveloping fragrance that opens with bergamot and orange blossom before settling into a heart of jasmine, amber, and golden musk. 50ml.")
                .imageUrl(IMG + "1541643600914-78b084683702?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("perfume","fragrance","warm","floral")).rating(4.9).reviewCount(198).build(),

            Product.builder()
                .name("Garden Notes Perfume").category("Beauty").subcategory("Fragrance")
                .price(88.0).description("A fresh, green floral that captures the feeling of wandering through a rain-kissed garden. Notes of violet leaf, peony, lily of the valley, and white cedar. 50ml.")
                .imageUrl(IMG + "1588776814546-1ffedbe47add?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("perfume","floral","fresh","green")).rating(4.8).reviewCount(156).build(),

            // === BEAUTY / MAKEUP ===
            Product.builder()
                .name("Tinted Lip Treatment").category("Beauty").subcategory("Makeup")
                .price(22.0).description("A nourishing lip balm-gloss hybrid tinted with the most perfect natural rose. SPF 30, enriched with jojoba oil and shea butter. The one product you'll always reach for.")
                .imageUrl(IMG + "1619451050621-83cb7aada2d7?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("lip balm","tinted","gloss","natural")).rating(4.9).reviewCount(678).build(),

            Product.builder()
                .name("Illuminating Setting Powder").category("Beauty").subcategory("Makeup")
                .price(42.0).clearancePrice(32.0).isClearance(true)
                .description("A finely-milled translucent setting powder with a subtle luminosity that photographs beautifully. Sets makeup for 16 hours while giving skin an airbrushed, lit-from-within finish.")
                .imageUrl(IMG + "1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=600&h=700")
                .tags(List.of("powder","setting","luminous","makeup")).rating(4.7).reviewCount(245).build()
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
