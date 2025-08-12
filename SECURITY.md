# Security Policy

## Supported Versions

We actively maintain and provide security updates for the following versions of Code Snapper API:

| Version | Supported          | Status |
| ------- | ------------------ | ------ |
| 0.0.x   | ✅ Development     | Active development with security fixes |
| 1.x.x   | ✅ Stable          | Full support with security updates |
| < 0.0.1 | ❌ No longer supported | Please upgrade to latest version |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security vulnerability in Code Snapper API, please report it responsibly.

### How to Report

**🔒 For security-sensitive issues:**

1. **DO NOT** create a public GitHub issue
2. **Email us directly** at: [repository owner's email - replace with actual email]
3. **Use encrypted communication** if possible (PGP key available on request)

**📧 Email Template:**
```
Subject: [SECURITY] Vulnerability Report for Code Snapper API

- Component affected:
- Vulnerability type:
- Impact assessment:
- Steps to reproduce:
- Suggested fix (if known):
```

### What to Include

Please include as much information as possible:

- **Affected component(s)**: API endpoint, dependency, etc.
- **Vulnerability type**: XSS, injection, DoS, etc.
- **Impact assessment**: Critical, High, Medium, Low
- **Reproduction steps**: Clear steps to reproduce
- **Proof of concept**: Code or requests demonstrating the issue
- **Suggested mitigation**: If you have ideas for fixes

### Response Timeline

We commit to the following response times:

| Severity | Initial Response | Investigation | Fix Release |
|----------|------------------|---------------|-------------|
| Critical | 24 hours | 72 hours | 7 days |
| High | 48 hours | 1 week | 2 weeks |
| Medium | 1 week | 2 weeks | 4 weeks |
| Low | 2 weeks | 4 weeks | Next release |

### Security Update Process

1. **Vulnerability Assessment**: We evaluate the report and confirm the issue
2. **Impact Analysis**: Determine severity and affected versions
3. **Fix Development**: Create and test security patches
4. **Security Advisory**: Prepare security advisory (if needed)
5. **Release**: Deploy fixes to supported versions
6. **Disclosure**: Public disclosure after fixes are available

## Security Best Practices

### For Users

**Deployment Security:**
- Always use the latest stable version
- Run with minimal required permissions
- Use HTTPS for all API communications
- Implement rate limiting in production
- Monitor for unusual API usage patterns

**Input Validation:**
- Validate all input parameters
- Sanitize code content before processing
- Implement request size limits
- Use proper content-type headers

**Environment Security:**
- Keep Java runtime updated
- Use official Docker images from ghcr.io
- Regularly scan for vulnerabilities
- Implement proper logging and monitoring

### For Contributors

**Code Security:**
- Follow secure coding practices
- Validate all user inputs
- Use parameterized queries/statements
- Avoid hardcoded secrets or credentials
- Implement proper error handling

**Dependency Management:**
- Keep dependencies updated
- Regularly audit for vulnerabilities
- Use dependency scanning tools
- Document security considerations in PRs

## Security Features

### Built-in Protections

**Input Sanitization:**
- Code content validation and sanitization
- File size limits for generated images
- Request rate limiting (when configured)
- Parameter validation for all endpoints

**Secure Defaults:**
- No persistent data storage by default
- Minimal attack surface
- Safe image generation process
- Memory usage limits

**Monitoring:**
- Structured logging for security events
- Error tracking without sensitive data exposure
- Request/response monitoring capabilities

### Recommended Deployment

**Container Security:**
```dockerfile
# Use official base images
FROM openjdk:17-jre-slim

# Run as non-root user
RUN adduser --system --group codesnapper
USER codesnapper

# Minimal permissions
COPY --chown=codesnapper:codesnapper app.jar /app/
```

**Network Security:**
- Deploy behind reverse proxy (nginx, cloudflare)
- Use TLS/HTTPS for all communications
- Implement proper CORS policies
- Consider API authentication for production use

## Vulnerability Disclosure Policy

### Coordinated Disclosure

We follow responsible disclosure practices:

1. **Private reporting** to maintainers first
2. **Coordinated timeline** for public disclosure
3. **Credit given** to security researchers (with permission)
4. **Public advisory** published after fixes are available

### CVE Process

For qualifying vulnerabilities:
- We will request CVE numbers when appropriate
- Security advisories will be published on GitHub
- Updates will be communicated through official channels

## Security Resources

### Tools and Scanning

**Automated Security Scanning:**
- GitHub Security Advisories enabled
- Dependabot security updates enabled
- Code scanning with CodeQL enabled
- Container vulnerability scanning in CI/CD

**Manual Security Testing:**
```bash
# Dependency vulnerability scan
./gradlew dependencyCheckAnalyze

# Static security analysis (if available)
./gradlew spotbugsMain

# Container security scan
docker scout cves ghcr.io/raquezha/codesnapper:latest
```

### External Resources

- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)
- [Kotlin Security Best Practices](https://kotlinlang.org/docs/security.html)
- [Ktor Security Guidelines](https://ktor.io/docs/security.html)

## Contact Information

For security-related questions or concerns:

- **Security issues**: [security email]
- **General questions**: Create a GitHub issue
- **Community discussion**: GitHub Discussions

---

**Last Updated**: December 2024  
**Next Review**: March 2025

Thank you for helping keep Code Snapper API secure! 🔒