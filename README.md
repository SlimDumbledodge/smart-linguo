# Smart Linguo API

<p align="center">
  <img src="./assets/logo.png" width="140" alt="SmartLinguo Logo" />
</p>

<p align="center">
  AI-powered translation infrastructure built for developers and global products.
</p>

---

## Overview

SmartLinguo API is a scalable translation platform powered by OpenAI, designed for applications that need fast, secure, and production-ready multilingual capabilities.

The platform provides:

- AI translations via OpenAI
- Token-based consumption model
- Stripe-powered payments
- Secure authentication with Keycloak
- API key generation and quota management
- Translation history tracking
- Cloud-ready infrastructure

This repository contains the core backend API built with Quarkus and Java 21.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Quarkus 3 |
| Language | Java 21 |
| Database | PostgreSQL |
| Authentication | Keycloak |
| Payments | Stripe |
| AI Provider | OpenAI |
| Containerization | Docker |
| Cloud | AWS |
| Observability | CloudWatch |

---

## Features

### AI Translation API
Translate content using OpenAI models through a unified SmartLinguo API.

### Secure API Keys
Generate and manage API keys linked to authenticated accounts.

### Token Quota System
Consumption-based architecture using prepaid translation credits.

### Stripe Integration
Automatic quota recharge after successful checkout sessions.

### Keycloak Authentication
Enterprise-grade identity and access management.

### Translation History
Track requests and monitor API usage over time.

### Production-Ready Infrastructure
Dockerized environment with AWS deployment support.

---

## Architecture

```text
Client Application
        │
        ▼
SmartLinguo API (Quarkus)
        │
 ┌──────┼──────┐
 ▼      ▼      ▼
OpenAI Stripe Keycloak
        │
        ▼
 PostgreSQL
