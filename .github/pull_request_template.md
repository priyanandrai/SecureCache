## Summary

- What changed?
- Why was this change needed?

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Security improvement
- [ ] Refactor / cleanup
- [ ] Documentation update
- [ ] Test update

## Paper Alignment (IJFMR260271936)

If applicable, reference the paper section(s) this PR aligns with:

- [ ] Runtime key generation
- [ ] AES encryption/decryption flow
- [ ] Key camouflage (`K XOR SHA256(C)`)
- [ ] Multi-stage jumbling
- [ ] Envelope/version handling
- [ ] Not paper-related

## Validation

- [ ] `mvn clean test` passes locally
- [ ] Added/updated tests for changed behavior
- [ ] No breaking API changes, or they are documented

### Test Output (paste key lines)

```text
# Example:
# Tests run: 82, Failures: 0, Errors: 0, Skipped: 0
```

## Checklist

- [ ] I read `CONTRIBUTING.md`
- [ ] I read `SECURITY.md` (if touching crypto/security logic)
- [ ] I updated docs (`README.md` / `AGENTS.md`) if behavior changed
- [ ] I did not include secrets or sensitive data

