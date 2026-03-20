---
name: Bug report
about: Report a defect in SecureCache encryption, caching, or API behaviour
title: '[BUG] '
labels: 'bug'
assignees: ''
---

**Describe the bug**
A clear and concise description of what the bug is.

**SecureCache version / commit**
e.g. `0.0.1-SNAPSHOT` · commit `abc1234`

**To Reproduce**
Minimal code snippet that reproduces the issue:

```java
SecureCache<String, byte[]> cache = new SecureCache<>();
cache.put("key", value);
byte[] result = cache.get("key"); // unexpected behaviour here
```

**Expected behaviour**
What you expected to happen.

**Actual behaviour**
What actually happened (stack trace, wrong value, null, etc.).

**Environment**
- OS: e.g. Ubuntu 22.04 / Windows 11
- Java version: e.g. OpenJDK 8u392
- Maven version: e.g. 3.9.6

**Additional context**
Any other relevant information (custom jumble functions, loader usage, TTL config, etc.).
