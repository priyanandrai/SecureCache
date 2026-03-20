# Security Policy

## Supported Versions

| Version | Supported |
| ------- | --------- |
| 0.0.1-SNAPSHOT (current) | ✅ |
| Earlier builds | ❌ |

This project is in active development. Only the latest source on the `main` branch receives security fixes.

## Cryptographic Design

SecureCache implements the **Dynamic Key Camouflage Encryption** model (IJFMR260271936).
Key security properties of the current implementation:

| Property | Detail |
|---|---|
| Encryption algorithm | AES/GCM/NoPadding (authenticated encryption) |
| Key size | 256-bit, runtime-generated per cache entry |
| Key derivation | PBKDF2WithHmacSHA256 |
| IV | 12-byte random per entry via `SecureRandom` |
| Key storage | None — key is XOR-bound to SHA-256(ciphertext) inside the blob |
| Tamper detection | GCM authentication tag (128-bit) rejects any modified ciphertext |
| Structural obfuscation | Multi-stage jumbling (up to 3 of 12 reversible functions) |

## Reporting a Vulnerability

**Do NOT open a public GitHub issue for security vulnerabilities.**

Report all security issues privately by email to:

**amitrai5100@gmail.com**

Please include:
- A clear description of the vulnerability
- Steps to reproduce or a proof-of-concept
- Affected version / commit hash
- Potential impact assessment

You can expect an acknowledgement within **72 hours** and a patch or mitigation plan within **14 days** for confirmed issues.

## Known Limitations (per paper §9)

- Security partially depends on algorithm secrecy (security-through-obscurity layer in jumbling)
- Structural obfuscation must avoid predictable patterns — custom jumble functions should be reviewed carefully
- Formal cryptographic proofs are outside the current scope; see paper §10 for future work
