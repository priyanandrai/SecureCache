# SecureCache

[![CI](https://github.com/priyanandrai/SecureCache/actions/workflows/ci.yml/badge.svg)](https://github.com/priyanandrai/SecureCache/actions/workflows/ci.yml)
[![CodeQL](https://github.com/priyanandrai/SecureCache/actions/workflows/codeql.yml/badge.svg)](https://github.com/priyanandrai/SecureCache/actions/workflows/codeql.yml)
[![License: Apache-2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

> Java implementation of **"Dynamic Key Camouflage Encryption: A Self-Contained Encryption Structure for Portable Secure Data Storage"**
> — Amit Rai, *IJFMR*, Volume 8 Issue 2, March–April 2026 · Paper ID: IJFMR260271936

A thread-safe, encrypted in-memory cache for Java 8+. Every cached value is serialized, AES/GCM-encrypted with a per-entry runtime-generated key, and stored as a self-contained **Jumbled Object** — no external key storage required.

---

## Table of Contents

1. [Paper Overview](#paper-overview)
2. [How It Works — Encryption Pipeline](#how-it-works--encryption-pipeline)
3. [Jumble Function Library](#jumble-function-library)
4. [Architecture](#architecture)
5. [Getting Started](#getting-started)
6. [API Reference](#api-reference)
7. [Custom Jumble Functions](#custom-jumble-functions)
8. [Project Structure](#project-structure)
9. [Build & Test](#build--test)
10. [Contributing](#contributing)
11. [License](#license)

---

## Paper Overview

Traditional cryptographic systems store or transmit encryption keys separately from the encrypted data, creating operational risk through memory dumps, config-file leaks, and key mismanagement.

This project implements the **Dynamic Key Camouflage** model proposed in the paper:

| Paper Concept | Implementation |
|---|---|
| Runtime key generation | `KeyGenerator` — PBKDF2WithHmacSHA256, 256-bit key per entry |
| AES encryption | `Cipher` — `AES/GCM/NoPadding`, 12-byte random IV |
| Ciphertext hashing | `Cipher.sha256(cipherText)` — SHA-256 |
| Key camouflage | `hiddenKey = K ⊕ SHA256(C)` |
| Structure composition | `D = C ‖ Kh` packed into versioned envelope |
| Multi-stage jumbling | Up to 3 randomly-selected functions from the 12-function library |
| Portable encrypted object | Self-contained blob stored in `TimeBasedHashMap` |

---

## How It Works — Encryption Pipeline

### Encryption (`put`)

```
Plaintext value
     │
     ▼
SerializationUtils.serialize()   ← Apache Commons Lang3
     │
     ▼
 AES/GCM/NoPadding encrypt       ← runtime key K, random 12-byte IV
     │
     ▼
 hiddenKey = K ⊕ SHA-256(C)      ← key camouflage (paper §3.4)
     │
     ▼
 frame = [ len(C) | C | hiddenKey ]
     │
     ▼
 Multi-stage jumbling             ← 3 random functions from F0–F11, indices stored
     │
     ▼
 Envelope = [ version | stageCount | stages[] | ivLen | IV | payloadLen | payload ]
     │
     ▼
 TimeBasedHashMap  (TTL-backed in-memory store)
```

### Decryption (`get`)

```
TimeBasedHashMap  →  encrypted blob
     │
     ▼
 Parse envelope (version check, read stages[], IV, payload)
     │
     ▼
 Reverse jumbling in stored sequence order (stageCount → 0)
     │
     ▼
 Split frame → C  +  hiddenKey
     │
     ▼
 K = hiddenKey ⊕ SHA-256(C)      ← key recovery (paper §6.4)
     │
     ▼
 AES/GCM decrypt with K, IV
     │
     ▼
 SerializationUtils.deserialize()
     │
     ▼
 Plaintext value returned
```

---

## Jumble Function Library

12 reversible functions are registered by default (F0–F11). The cipher randomly selects up to 3 per encryption and stores the selection sequence in the envelope for deterministic reversal.

| ID | Class | Algorithm | Self-inverse |
|----|-------|-----------|:---:|
| F0 | `Stringmagic` | Identity (pass-through) | ✅ |
| F1 | `StringLogic` | Block Swap — `[A\|B] → [B\|A]` | ✅ |
| F2 | `Stringtrick` | Byte Reversal — reverse all bytes | ✅ |
| F3 | `CircularRotationJumble` | Circular left rotation by 3 positions | ❌ (rotate right) |
| F4 | `EvenOddJumble` | Even-Odd Permutation — even indices first, then odd | ❌ (de-interleave) |
| F5 | `XorMaskJumble` | XOR Masking — `result[i] = data[i] ⊕ i` | ✅ |
| F6 | `NibbleSwapJumble` | Nibble Swap — swap high/low 4 bits per byte | ✅ |
| F7 | `ByteInversionJumble` | Byte Inversion — bitwise NOT per byte | ✅ |
| F8 | `PairSwapJumble` | Adjacent Pair Swap — swap `(0,1), (2,3), …` | ✅ |
| F9 | `TriBlockJumble` | Tri-Block Rotation — `[A][B][C] → [B][C][A]` | ❌ (rotate back) |
| F10 | `InterleaveJumble` | Interleave Halves — `[A1,A2,B1,B2] → [A1,B1,A2,B2]` | ❌ (de-interleave) |
| F11 | `CaesarByteJumble` | Caesar Byte Shift — add 83 mod 256 per byte | ❌ (subtract 83) |

Every function satisfies `F⁻¹(F(x)) = x`, as required by the paper (§4).

---

## Architecture

```
┌──────────────────────────────────────────┐
│              SecureCache<K,V>            │  ← public API: put / get / remove
│  ┌────────────────┐  ┌────────────────┐  │
│  │  Cipher        │  │TimeBasedHashMap│  │
│  │  protectData() │  │  putValue()    │  │
│  │  revealData()  │  │  getValue()    │  │
│  └───────┬────────┘  │  removeValue() │  │
│          │           └────────────────┘  │
│  ┌───────▼────────┐                      │
│  │  KeyGenerator  │  PBKDF2 → AES-256    │
│  └────────────────┘                      │
│  ┌────────────────────────────────────┐  │
│  │  JumbleFunctionInterface (F0–F11)  │  │
│  └────────────────────────────────────┘  │
│  ┌────────────────┐                      │
│  │  SourcesLoader │  optional lazy load  │
│  └────────────────┘                      │
└──────────────────────────────────────────┘
```

**Package layout:**

| Package | Responsibility |
|---|---|
| `com.securecache.main` | `SecureCache`, `SecureCacheBuilder`, `Constant` |
| `com.securecache.cipher` | `Cipher` (AES/GCM + key camouflage), `KeyGenerator` |
| `com.securecache.JumbleFunction` | 12 jumble function implementations |
| `com.securecache.secureinterface` | `JumbleFunctionInterface` |
| `com.securecache.dataHandler` | `TimeBasedHashMap`, `ValueWithTimestamp` |
| `com.securecache.Loader` | `SourcesLoader` (lazy-load callback) |
| `com.securecache.utility` | `Utils` (byte-array helpers) |

---

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven 3.x

### Installation

```bash
git clone https://github.com/priyanandrai/SecureCache.git
cd SecureCache
mvn clean install
```

### Quick Usage

**Without loader (manual put/get):**

```java
import com.securecache.main.SecureCache;

SecureCache<String, byte[]> cache = new SecureCache<>();

// Encrypt and store
cache.put("session-token", tokenBytes);

// Decrypt and retrieve
byte[] token = cache.get("session-token");

// Remove
cache.remove("session-token");
```

**With lazy loader (auto-fetch on cache miss):**

```java
import com.securecache.Loader.SourcesLoader;
import com.securecache.main.SecureCache;

SourcesLoader<String, byte[]> loader = new SourcesLoader<String, byte[]>() {
    private static final long serialVersionUID = 1L;

    @Override
    public byte[] loadValue(String key) {
        return fetchFromDatabase(key); // called only on cache miss
    }
};

SecureCache<String, byte[]> cache = new SecureCache
        .SecureCacheBuilder<String, byte[]>()
        .Loader(loader)
        .build();

byte[] value = cache.get("user-profile-123"); // loads, encrypts, and caches automatically
```

---

## API Reference

| Method | Description |
|---|---|
| `void put(Key key, Value value)` | Serialize, encrypt, and store value. No-op for `null` values. Value must implement `Serializable`. |
| `Value get(Key key)` | Decrypt and return cached value. On miss, calls `SourcesLoader` (if set) and backfills cache. Returns `null` on miss or error. |
| `boolean remove(Key key)` | Remove entry. Returns `true` if found and removed, `false` otherwise. |
| `void setJumbleFunction(ArrayList<JumbleFunctionInterface> fns)` | Replace the default F0–F11 list with a custom function chain. |

All three mutation methods (`put`, `get`, `remove`) are `synchronized` for thread safety.

---

## Custom Jumble Functions

Implement `JumbleFunctionInterface` to add your own reversible transformation:

```java
import com.securecache.secureinterface.JumbleFunctionInterface;

public class MyJumble implements JumbleFunctionInterface {

    @Override
    public byte[] jumbleData(byte[] data) {
        // apply transformation — must be exactly reversible
        return transformed;
    }

    @Override
    public byte[] reassemble(byte[] data) {
        // exact inverse of jumbleData
        return original;
    }
}
```

Register with the cache:

```java
ArrayList<JumbleFunctionInterface> custom = new ArrayList<>();
custom.add(new MyJumble());
cache.setJumbleFunction(custom);
```

**Contract:** `reassemble(jumbleData(x))` must equal `x` for all byte arrays `x`.

---

## Project Structure

```
SecureCache/
├── pom.xml
├── README.md
├── AGENTS.md
└── src/
    ├── main/java/com/securecache/
    │   ├── main/
    │   │   ├── SecureCache.java        ← public API + builder
    │   │   └── Constant.java           ← CACHE_VERSION, KEY_LENGTH
    │   ├── cipher/
    │   │   ├── Cipher.java             ← AES/GCM + key camouflage
    │   │   └── KeyGenerator.java       ← PBKDF2 runtime key generation
    │   ├── JumbleFunction/
    │   │   ├── Stringmagic.java        ← F0  Identity
    │   │   ├── StringLogic.java        ← F1  Block Swap
    │   │   ├── Stringtrick.java        ← F2  Byte Reversal
    │   │   ├── CircularRotationJumble  ← F3  Circular Rotation
    │   │   ├── EvenOddJumble.java      ← F4  Even-Odd Permutation
    │   │   ├── XorMaskJumble.java      ← F5  XOR Masking
    │   │   ├── NibbleSwapJumble.java   ← F6  Nibble Swap
    │   │   ├── ByteInversionJumble.java← F7  Byte Inversion
    │   │   ├── PairSwapJumble.java     ← F8  Adjacent Pair Swap
    │   │   ├── TriBlockJumble.java     ← F9  Tri-Block Rotation
    │   │   ├── InterleaveJumble.java   ← F10 Interleave Halves
    │   │   └── CaesarByteJumble.java   ← F11 Caesar Byte Shift
    │   ├── secureinterface/
    │   │   └── JumbleFunctionInterface.java
    │   ├── dataHandler/
    │   │   └── TimeBasedHashMap.java   ← TTL-aware HashMap
    │   ├── Loader/
    │   │   └── SourcesLoader.java      ← lazy-load callback
    │   └── utility/
    │       └── Utils.java
    └── test/java/
        ├── JumbleFunctionRoundtripTest.java  ← 47 tests: all F0–F11
        ├── CipherPaperModelTest.java         ←  7 tests: AES/GCM + tamper
        ├── SecureCacheCallFunctionalityTest  ← 10 tests: API behaviour
        ├── SecureCacheConcurrencyTest.java   ←  5 tests: thread safety
        └── TimeMapTest.java                  ← 13 tests: TTL lifecycle
```

---

## Build & Test

```bash
# Clean build + run all 82 tests
mvn clean test

# Package the library
mvn clean package
```

Always use `mvn clean test` (not `mvn test`) to avoid stale compiled classes producing false-positive results.

**Test suite summary:**

| Test class | Tests | Coverage area |
|---|:---:|---|
| `JumbleFunctionRoundtripTest` | 47 | All 12 jumble functions — roundtrip, transformation, null safety, chained |
| `SecureCacheCallFunctionalityTest` | 10 | put/get/remove API, loader memoization, null handling, custom jumble |
| `SecureCacheConcurrencyTest` | 5 | Thread-safe put/get/remove, loader call-once under race |
| `CipherPaperModelTest` | 7 | AES/GCM roundtrip, randomness, tamper detection, null/empty guards |
| `TimeMapTest` | 13 | TTL lifecycle, lazy expiry, overwrite, size, containsKey |
| **Total** | **82** | |

---

## Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/my-change`.
3. Commit your changes.
4. Open a pull request with a description of your change.

Please read [CONTRIBUTING.md](CONTRIBUTING.md) before submitting.
Report security issues privately to **amitrai5100@gmail.com** — do not open public issues for vulnerabilities.

---

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

---

> **Reference:** Amit Rai, *"Dynamic Key Camouflage Encryption: A Self-Contained Encryption Structure for Portable Secure Data Storage"*, International Journal for Multidisciplinary Research (IJFMR), E-ISSN 2582-2160, Volume 8 Issue 2, March–April 2026. Paper ID: IJFMR260271936. [www.ijfmr.com](https://www.ijfmr.com)
