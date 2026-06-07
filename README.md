# CartDetox 🛍️

### A fake shopping website for people who love shopping a little too much.

---

## The idea

I have a habit. Not a bad one, exactly, but I constantly find myself opening shopping websites, filling up my cart, and then closing the tab because I know I shouldn't actually buy anything. The urge is satisfied for about five minutes, and then I'm back at it.

So I built CartDetox — a fully fake fashion and lifestyle boutique that lets you shop without spending a single real dollar. Browse real-looking products, add them to your cart, fill in a fake credit card, and check out. The dopamine hit, none of the buyer's remorse.

But here's the twist: you have to *earn* your spending money first. CartDetox gives you **Detox Tokens (DT)**: virtual currency you build up by completing daily challenges like the spin wheel (very Temu-coded), mindfulness check-ins, journaling, going outside, reading, and other activities that are genuinely good for you. The idea is that the app redirects the shopping urge into something healthier, even if just for a few minutes.

You can track every fake purchase you've made, see how much you've "spent," and feel the full satisfaction of a checkout confirmation — shipping address, order number, everything — without touching your actual wallet.

---

## Features

- **Full shopping experience** — browse 56+ fashion and lifestyle products across Clothing, Accessories, Shoes, Beauty, and Lifestyle
- **Spin the wheel** — daily spin for bonus Detox Tokens (once per day, just like the real apps)
- **Rewards & challenges** — earn tokens by completing mindfulness, creativity, fitness, and personal growth activities
- **Clearance section** — rotating sale items with discounted token prices
- **Fake checkout** — fake credit card form, fake shipping, real confirmation with a tracking number
- **Order history** — track everything you've "bought"
- **User accounts** — register, log in, see your token balance and purchase history

---

## Tech Stack

| Layer | Tech |
|---|---|
| Backend | Spring Boot 3.2 (Java 17) |
| Frontend | Angular 16 |
| Database | H2 (dev) / PostgreSQL (prod) |
| Auth | JWT |
| Backend hosting | Railway |
| Frontend hosting | Vercel |
| Version control | Git / GitHub |

---

## Running locally

### Prerequisites
- Java 17+
- Node.js 18+
- Maven (or use the included `./mvnw` wrapper)

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Runs on `http://localhost:8080`. Uses H2 in-memory database by default — no setup needed. The database seeds itself with products and reward activities on first run.

### Frontend

```bash
cd frontend
npm install
npm start
```

Runs on `http://localhost:4200`. API calls are proxied to `localhost:8080` automatically.

### Environment variables (production only)

For Railway deployment, set these in your service settings:

| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://...` (Railway provides this) |
| `SPRING_DATASOURCE_USERNAME` | from Railway PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | from Railway PostgreSQL |
| `JWT_SECRET` | any long random string |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |

---

## A note on how this was built

This was my first time using **Claude Code** to build a full stack application end to end — and honestly, it was a really cool experience. I came in with an idea and a folder structure, and we figured out the architecture, wrote the code, debugged the build errors, and got it deployed together.

It's genuinely impressive what you can get done when you're not starting from a blank file. That said, the creative part — *what* to build, *why* it should exist, the personality of the app — that's still the human side of it. Tools like this are most useful when you actually have something to say. I wanted to make something that felt real and had a point to it, and Claude helped me get there faster than I could have on my own.

If you've ever rage-added things to a cart at 1am and then felt guilty about it — this one's for you.

---

*Built with Spring Boot + Angular · Deployed on Railway + Vercel*
