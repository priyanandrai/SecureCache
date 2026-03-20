# AGENTS.md

## Project Snapshot
- Java 8-era Maven library for encrypted in-memory caching (`pom.xml`, `src/main/java`).
- Core entry point is `com.securecache.main.SecureCache<Key, Value>`.
- There are no repo-specific AI instruction files; this guide is derived from `README.md` and source code.

## Architecture: What Talks to What
- `SecureCache` orchestrates the full pipeline: serialize -> encrypt/jumble -> store (`put`) and fetch -> de-jumble/decrypt -> deserialize (`get`).
- Storage is `TimeBasedHashMap<Key, Value>` in `src/main/java/com/securecache/dataHandler/TimeBasedHashMap.java`; entries are wrapped in `ValueWithTimestamp` and expire on read.
- Encryption is in `src/main/java/com/securecache/cipher/Cipher.java` using AES plus a generated key from `KeyGenerator` (PBKDF2).
- Obfuscation stage is pluggable via `JumbleFunctionInterface`; default chain is `Stringmagic`, `StringLogic`, `Stringtrick` initialized in `SecureCache` constructors.
- Cache-miss loading boundary is `SourcesLoader<Key, Value>#loadValue` (`src/main/java/com/securecache/Loader/SourcesLoader.java`), wired through `SecureCacheBuilder.Loader(...)`.

## Data Flow Details That Matter
- `SecureCache.put` casts values to `Serializable` before `SerializationUtils.serialize`; non-serializable values will fail at runtime.
- `SecureCache.get` first reads encrypted bytes from map, otherwise calls `SourcesLoader` under synchronization and backfills cache.
- `Cipher.protectData` now builds a versioned envelope: `version | stage-sequence | IV | jumbled-payload`.
- The payload is paper-aligned: `ciphertext` + hidden key where `hiddenKey = key XOR SHA256(ciphertext)`.
- `TimeBasedHashMap.get` performs lazy expiration (`plusSeconds(expirationTimeInSeconds)`), so eviction happens only when keys are accessed.

## Build/Test Workflow (Observed)
- Main build command: `mvn clean test`.
- Current source compiles from clean; `mvn clean test` passes with demo-style tests.
- `mvn test` can still appear green with stale classes in `target/`, so use clean builds for reliable validation.
- Test classes (`src/test/java/Test.java`, `src/test/java/RetriveDataFromSources.java`) are executable demos with `main`, not JUnit assertions.

## Local Conventions (Non-Standard)
- Naming is intentionally inconsistent with Java norms in places: `Loader` package name is capitalized and builder method is `Loader(...)`.
- `JumbleFunctionInterface` keeps backward-compatible aliases (`JumbleData`, `Reassbamble`) but canonical methods are `jumbleData` and `reassemble`.
- Public API behavior uses null/boolean fallbacks instead of rich exceptions (`SecureCache.get` returns `null` on failure, `remove` returns boolean).
- TTL is hard-coded in `SecureCache` constructor (`new TimeBasedHashMap<>(10000000)`), not currently exposed via builder config.

## Paper Alignment (IJFMR260271936)
- Encryption is now `AES/GCM/NoPadding` with per-entry random IV (`src/main/java/com/securecache/cipher/Cipher.java`).
- Key material is runtime-generated and camouflaged via XOR-hash binding (`K XOR SHA256(C)`) instead of plaintext embedding.
- Multi-stage jumbling is applied with a stored function-index sequence; decryption reverses sequence order.
- Envelope versioning is explicit (`Constant.CACHE_VERSION`) to support future format migration.

## First Files To Read Before Changing Logic
- `src/main/java/com/securecache/main/SecureCache.java`
- `src/main/java/com/securecache/cipher/Cipher.java`
- `src/main/java/com/securecache/dataHandler/TimeBasedHashMap.java`
- `src/main/java/com/securecache/secureinterface/JumbleFunctionInterface.java`
- `src/test/java/Test.java`

