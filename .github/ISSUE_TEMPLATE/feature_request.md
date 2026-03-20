---
name: Feature request
about: Suggest an enhancement to SecureCache — new jumble functions, API additions, TTL config, etc.
title: '[FEATURE] '
labels: 'enhancement'
assignees: ''
---

**Is your feature request related to a problem? Please describe.**
A clear description of what the problem is.
e.g. "There is no way to configure TTL per-entry; it is hard-coded to 10 000 000 seconds."

**Describe the solution you'd like**
A clear and concise description of what you want to happen, including any API changes.

```java
// example of proposed API usage
SecureCache<String, byte[]> cache = new SecureCache.SecureCacheBuilder<String, byte[]>()
        .ttlSeconds(3600)
        .build();
```

**Paper alignment**
If relevant, reference the section of IJFMR260271936 that motivates or supports the feature.
e.g. "Paper §10 Future Work mentions hardware-bound encryption via TPM."

**Describe alternatives you've considered**
Any alternative designs or workarounds you have considered.

**Additional context**
Any other context, diagrams, or references.
