# <img src="favicon.svg" width="28" align="center" /> SmartLinguo

**AI-powered multilingual translation API.**  
Buy credits, get an API key, translate.

---

## What it is

SmartLinguo is a translation SaaS API. Buy a token pack, receive an API key, call `/translate`. That's it.

```bash
curl -X POST https://api.smartlinguo.com/translate \
  -H "X-API-Key: sl_xxxxxxxxxxxxxxxx" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Hello, world!",
    "sourceLanguage": "en",
    "targetLanguages": ["fr", "es", "de"]
  }'
```

```json
{
  "fr": "Bonjour, le monde !",
  "es": "¡Hola, mundo!",
  "de": "Hallo, Welt!"
}
```

---

## Features

- **Multi-language translation** in a single API call
- **Credit-based model** — buy what you need, no subscription
- **Dedicated API key** per account, generated from the dashboard
- **Real-time quota** — check your remaining tokens at any time
- **Secure payments** via Stripe
- **Authentication** via Keycloak (OpenID Connect)

---

## Pricing

| Plan | Tokens | Price |
|---|---|---|
| Starter | 50,000 | €9.99 |
| Pro | 500,000 | €24.99 |
| Business | 5,000,000 | €99.99 |

Tokens map to OpenAI tokens consumed by your translation requests. A typical call uses between 100 and 500 tokens depending on text length.

---

## Stack

| | |
|---|---|
| **API** | Quarkus 3 (reactive, Mutiny) · Java 21 |
| **AI** | OpenAI API |
| **Auth** | Keycloak 26 (OpenID Connect) |
| **Payments** | Stripe |
| **Database** | PostgreSQL 15 |
| **Infrastructure** | AWS — EC2, RDS, ALB, Route 53, ACM |
| **Observability** | CloudWatch (logs, metrics, alarms) |

---

## Quick Start

**1. Buy a plan** at [smartlinguo.com](#) — you'll receive an email to set your password.

**2. Log in** to the dashboard and generate your API key.

**3. Call the API:**

```bash
curl -X POST https://api.smartlinguo.com/translate \
  -H "X-API-Key: YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Good morning!",
    "sourceLanguage": "en",
    "targetLanguages": ["fr", "es"]
  }'
```

---

## Documentation

| | |
|---|---|
| 🔑 [Get an API key](#) | Create your account and generate your key |
| 📖 [API Reference](#) | All endpoints, parameters and error codes |
| 💳 [Plans & Pricing](#) | Compare plans |

---

## Contributing

This repository contains the Quarkus API source code. To run the local development environment, see the [contribution guide](CONTRIBUTING.md).

---

## License

[MIT](LICENSE)
