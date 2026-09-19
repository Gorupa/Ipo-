# IPO Ninja data collector

This folder is intentionally a starter architecture, not a scraper of a third-party site.

Recommended pipeline:
1. Fetch only public data from sources whose terms permit automated access and redistribution.
2. Respect robots.txt, rate limits and source terms.
3. Normalize IPO records into the backend schema.
4. Store source URL/name and fetched timestamp.
5. Cross-check GMP when possible.
6. Publish normalized data through the Render API.

Do not bypass CAPTCHAs, authentication, paywalls, robots restrictions, rate limits or technical access controls.
