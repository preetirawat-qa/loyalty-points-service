# Loyalty Points Quote Service

A Vert.x-based microservice that calculates loyalty points for a flight booking.
Implements tier bonuses, promo bonuses, FX conversion, validation, and resilience.

---

## 🚀 Features

- **POST /v1/points/quote**
- Base points calculated *after FX conversion*
- Tier multipliers:
  - NONE=0%, SILVER=15%, GOLD=30%, PLATINUM=50%
- Promo system:
  - External promo lookup
  - Expiry warnings (≤ 7 days)
  - 300 ms timeout fallback → promo unavailable
- FX:
  - External FX lookup
  - Retry 3× with backoff
- Total points capped at **50,000**
- Full component test suite with:
  - JUnit 5
  - WireMock
  - VertxExtension
  - AssertJ
- ≥ 80% branch coverage via JaCoCo

---

## 📦 Build

```bash
mvn clean package
