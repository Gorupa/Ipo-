# IPO Ninja data model

Each IPO should contain:
- identity: name, symbol, type
- pricing: priceLow, priceHigh, lotSize
- gmp: value, percent, source, updatedAt
- subscription: QIB, NII, retail, employee, overall
- dates: open, close, allotment, refund, listing
- issueSize
- registrar

Calculated values:
- estimatedListing = priceHigh + gmp
- estimatedGainPerShare = gmp
- estimatedGainPerLot = gmp * lotSize
- investmentPerLot = priceHigh * lotSize

Always label GMP-based values as estimates.
