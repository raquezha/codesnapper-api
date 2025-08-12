# Repository Settings & Configuration Guide

This document provides a comprehensive guide for configuring repository settings, branch protection rules, and access control for the Code Snapper API repository.

## Table of Contents

- [Branch Protection Rules](#branch-protection-rules)
- [Repository Access Control](#repository-access-control)
- [Security Settings](#security-settings)
- [Automation & Integrations](#automation--integrations)
- [Repository Management](#repository-management)

## Branch Protection Rules

### Configuring Main Branch Protection

Navigate to **Settings** → **Branches** → **Add rule** for `main` branch:

#### Required Settings

**Branch name pattern**: `main`

**Protection Rules:**
- ✅ **Require a pull request before merging**
  - [x] Require approvals: `1`
  - [x] Dismiss stale PR approvals when new commits are pushed
  - [x] Require review from code owners
  - [x] Restrict pushes that create files larger than `100 MB`

- ✅ **Require status checks to pass before merging**
  - [x] Require branches to be up to date before merging
  - **Required status checks:**
    ```
    code-quality / Code Quality Checks
    test / Run Tests
    build / Build Application
    ```

- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits** (recommended for security)
- ✅ **Require linear history** (prevents merge commits)
- ✅ **Do not allow bypassing the above settings**
- ✅ **Restrict pushes that create files larger than 100 MB**

#### Rules for Administrators
- [ ] Allow administrators to bypass these requirements (not recommended)
- [x] Include administrators (recommended)

### Optional: Develop Branch Protection

For teams using GitFlow, configure similar protection for `develop`:

**Branch name pattern**: `develop`

**Simplified Protection:**
- ✅ **Require a pull request before merging**
  - [x] Require approvals: `1`
  - [x] Dismiss stale PR approvals when new commits are pushed

- ✅ **Require status checks to pass before merging**
  - [x] Same status checks as main branch

## Repository Access Control

### Managing Collaborators

Navigate to **Settings** → **Manage access**:

#### Access Levels

**Admin** (Repository Owner + Lead Maintainers)
- Full access to repository settings
- Can manage collaborators and teams
- Can merge without restrictions (if bypass is enabled)
- Can delete the repository

**Write** (Core Contributors)
- Can push to repository
- Can merge pull requests
- Can manage issues and pull requests
- Cannot change repository settings

**Triage** (Community Moderators)
- Can manage issues and pull requests
- Can apply labels and assign issues
- Cannot push code or merge PRs

**Read** (All Contributors)
- Can clone and fork repository
- Can create issues and pull requests
- Cannot push directly to repository

#### Team Management

Create teams for better access management:

1. **Core Team** (Write access)
   - Lead developers
   - Active maintainers
   - Trusted contributors

2. **Moderators** (Triage access)
   - Issue moderators
   - Community managers
   - Documentation maintainers

3. **Contributors** (Read access)
   - External contributors
   - Community members
   - Bug reporters

### Base Permissions

**Settings** → **Manage access** → **Base permissions**:
- Set to **Read** for public repositories
- This allows anyone to fork and create PRs

## Security Settings

### Security & Analysis

Navigate to **Settings** → **Security & analysis**:

#### Required Security Features
- ✅ **Vulnerability alerts** - Get notified of vulnerabilities
- ✅ **Dependency graph** - Visualize dependencies
- ✅ **Dependabot alerts** - Automated vulnerability detection
- ✅ **Dependabot security updates** - Automated security PRs

#### Code Scanning (GitHub Advanced Security)
- ✅ **Code scanning alerts** - CodeQL analysis
- ✅ **Secret scanning alerts** - Detect committed secrets
- ✅ **Dependency review** - Review dependency changes in PRs

#### Secret Scanning Configuration

Create `.github/secret-scanning.yml` (if needed):
```yaml
# Custom secret scanning patterns
patterns:
  - name: "Custom API Key"
    regex: "api_key_[a-zA-Z0-9]{32}"
    confidence: "high"
```

### Secrets Management

Navigate to **Settings** → **Secrets and variables** → **Actions**:

#### Repository Secrets
- `GITHUB_TOKEN` (automatically provided)
- Any custom deployment secrets
- Docker registry credentials (if not using GITHUB_TOKEN)

#### Organization Secrets (if applicable)
- Shared across multiple repositories
- Deployment credentials
- Third-party service tokens

## Automation & Integrations

### GitHub Actions Configuration

#### Required Workflows
Current workflows in `.github/workflows/`:
- `ci.yml` - Continuous integration
- `docker-build.yml` - Docker image building
- `release.yml` - Release automation

#### Workflow Permissions

**Settings** → **Actions** → **General**:

**Actions permissions:**
- ✅ Allow all actions and reusable workflows

**Workflow permissions:**
- ✅ Read repository contents and packages permissions
- [x] Allow GitHub Actions to create and approve pull requests

### Branch Protection Integration

Ensure GitHub Actions can interact with protected branches:

1. **Status checks** are configured to require workflow completion
2. **Actions** have appropriate permissions in workflow files
3. **GITHUB_TOKEN** has sufficient permissions for automation

### Third-party Integrations

#### Recommended Integrations

**Code Quality:**
- CodeClimate (code quality metrics)
- SonarCloud (static analysis)
- Codecov (test coverage)

**Security:**
- Snyk (vulnerability scanning)
- WhiteSource (license compliance)
- GitGuardian (secret detection)

**Project Management:**
- Linear (issue tracking)
- Notion (documentation)
- Slack (notifications)

## Repository Management

### General Settings

Navigate to **Settings** → **General**:

#### Repository Details
- **Description**: Clear, concise project description
- **Website**: Link to documentation or demo
- **Topics**: Relevant tags for discoverability
- **License**: Appropriate open source license

#### Features
- ✅ **Wikis** (if using GitHub wiki)
- ✅ **Issues** (for bug tracking and features)
- ✅ **Sponsorships** (if accepting donations)
- ✅ **Preserve this repository** (GitHub Archive Program)
- ✅ **Discussions** (for community Q&A)

#### Pull Requests
- ✅ **Allow merge commits**
- ✅ **Allow squash merging** (recommended)
- ✅ **Allow rebase merging**
- [x] **Always suggest updating pull request branches**
- [x] **Allow auto-merge**
- [x] **Automatically delete head branches**

#### Archives
- [x] **Include Git LFS objects in archives**

### Repository Templates

#### Issue Templates
Templates created in `.github/ISSUE_TEMPLATE/`:
- `bug_report.md` - Structured bug reporting
- `feature_request.md` - Feature proposal template

#### Pull Request Template
Template in `.github/pull_request_template.md`:
- Standardizes PR descriptions
- Includes checklists for contributors
- Ensures consistent review process

### Notifications

#### Email Notifications
**Settings** → **Notifications**:
- Configure which events trigger emails
- Set up team-specific notification rules

#### Webhooks
**Settings** → **Webhooks**:
- Integration with external services
- Slack/Discord notifications
- CI/CD trigger endpoints

## Advanced Configuration

### Custom Domain (GitHub Pages)

If hosting documentation:
1. **Settings** → **Pages**
2. Configure source branch
3. Set custom domain (if applicable)
4. Enable HTTPS enforcement

### Code Scanning Configuration

Create `.github/codeql/codeql-config.yml`:
```yaml
name: "Code scanning config"
queries:
  - uses: security-and-quality
  - uses: security-extended
paths-ignore:
  - "docs/**"
  - "**/*.md"
```

### Dependabot Configuration

Create `.github/dependabot.yml`:
```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    reviewers:
      - "raquezha"
    labels:
      - "dependencies"
      - "automated"
```

## Troubleshooting

### Common Issues

**Status checks not appearing:**
- Ensure workflow names match exactly
- Check that workflows run on PR events
- Verify GITHUB_TOKEN permissions

**Branch protection bypass:**
- Check admin bypass settings
- Verify user roles and permissions
- Review organization policies

**Failed automated merges:**
- Check branch protection conflicts
- Verify CI/CD pipeline status
- Review PR requirements completion

### Verification Checklist

After configuring repository settings:

- [ ] Create test PR to verify protection rules
- [ ] Confirm status checks are required and working
- [ ] Test that non-admin users cannot bypass rules
- [ ] Verify automated workflows complete successfully
- [ ] Check that issue/PR templates render correctly
- [ ] Confirm security scanning is detecting issues
- [ ] Test Dependabot is creating update PRs

---

**Configuration Version**: v1.0  
**Last Updated**: December 2024  
**Next Review**: March 2025

For questions about repository configuration, please create an issue or start a discussion.