# Contributing to Code Snapper API

Thank you for your interest in contributing to Code Snapper API! This guide will help you understand our development process, repository settings, and how to make meaningful contributions.

## Table of Contents

- [Repository Settings & Governance](#repository-settings--governance)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Code Quality Standards](#code-quality-standards)
- [Testing Requirements](#testing-requirements)
- [Submitting Changes](#submitting-changes)
- [Release Process](#release-process)

## Repository Settings & Governance

### Branch Protection Rules

We recommend the following branch protection settings for maintaining code quality:

#### Main Branch Protection
- **Require pull request reviews before merging**: ✅ Enabled
  - Required number of reviewers: **1**
  - Dismiss stale reviews when new commits are pushed: ✅ Enabled
  - Require review from code owners: ✅ Enabled (when CODEOWNERS file exists)

- **Require status checks to pass before merging**: ✅ Enabled
  - Required status checks:
    - `code-quality / Code Quality Checks`
    - `test / Run Tests` 
    - `build / Build Application`
  - Require branches to be up to date before merging: ✅ Enabled

- **Require conversation resolution before merging**: ✅ Enabled
- **Require signed commits**: ✅ Recommended
- **Require linear history**: ✅ Enabled (prevents merge commits)
- **Do not allow bypassing the above settings**: ✅ Enabled
- **Restrict pushes that create files larger than 100 MB**: ✅ Enabled

#### Develop Branch Protection (Optional)
For teams using GitFlow:
- **Require pull request reviews before merging**: ✅ Enabled
  - Required number of reviewers: **1**
- **Require status checks to pass before merging**: ✅ Enabled
  - Same status checks as main branch

### Repository Access Control

#### Team Permissions
- **Maintainers**: Admin access (release management, settings)
- **Core Contributors**: Write access (direct push to develop, create releases)
- **Contributors**: Triage access (manage issues and PRs)
- **Community**: Read access (clone, fork, create issues)

#### Collaborator Guidelines
- **Admin access**: Reserved for repository owner and lead maintainers
- **Write access**: For trusted contributors with merge privileges
- **Triage access**: For community moderators and issue managers

### Security Settings

- **Vulnerability alerts**: ✅ Enabled
- **Security updates**: ✅ Enabled
- **Code scanning alerts**: ✅ Enabled
- **Secret scanning alerts**: ✅ Enabled
- **Dependency review**: ✅ Enabled

## Getting Started

### Prerequisites

- **Java 17** or higher
- **Git** for version control
- **Docker** (optional, for containerized development)

### Setting Up Development Environment

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR_USERNAME/codesnapper-api.git
   cd codesnapper-api
   ```

2. **Set Up Upstream Remote**
   ```bash
   git remote add upstream https://github.com/raquezha/codesnapper-api.git
   ```

3. **Install Dependencies**
   ```bash
   ./gradlew build
   ```

4. **Verify Setup**
   ```bash
   ./gradlew test
   ./test-automation.sh
   ```

### IDE Configuration

#### IntelliJ IDEA (Recommended)
1. Import project as Gradle project
2. Set Project SDK to Java 17
3. Enable Kotlin plugin
4. Install ktlint plugin for code formatting
5. Configure code style: `Settings → Code Style → Kotlin → Set from → Predefined Style → ktlint`

#### VS Code
1. Install extensions:
   - Kotlin Language Support
   - Gradle for Java
   - ktlint extension

## Development Workflow

### Branching Strategy

We use **GitHub Flow** with optional feature branches:

```
main (protected)
├── feature/add-new-theme
├── bugfix/fix-material-design
└── docs/update-api-docs
```

#### Branch Naming Convention
- **Feature branches**: `feature/description-here`
- **Bug fixes**: `bugfix/description-here`
- **Documentation**: `docs/description-here`
- **Hotfixes**: `hotfix/description-here`

### Development Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Follow code quality standards
   - Write tests for new functionality
   - Update documentation if needed

3. **Test Locally**
   ```bash
   ./gradlew clean build test
   ./test-automation.sh
   ./test-material-design.sh
   ```

4. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: add new background theme support"
   ```

5. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   ```

### Commit Message Convention

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

[optional body]

[optional footer]
```

#### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

#### Examples
```bash
feat: add cyberpunk background theme
fix: resolve material design color contrast issue
docs: update API documentation for new themes
test: add unit tests for theme validation
```

## Code Quality Standards

### Kotlin Code Style

We use **ktlint** for code formatting:

```bash
# Check formatting
./gradlew ktlintCheck

# Auto-fix formatting issues
./gradlew ktlintFormat
```

### Static Analysis

We use **detekt** for static code analysis:

```bash
# Run static analysis
./gradlew detekt

# View report
open build/reports/detekt/detekt.html
```

### Code Quality Checks

All code must pass:
- ✅ ktlint formatting checks
- ✅ detekt static analysis
- ✅ No compiler warnings
- ✅ Test coverage requirements

## Testing Requirements

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run automation tests
./test-automation.sh

# Check Material Design compliance
./test-material-design.sh
```

### Test Categories

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test API endpoints and workflows
3. **Automation Tests**: End-to-end testing with actual image generation
4. **Compliance Tests**: Material Design and accessibility validation

### Coverage Requirements

- **Minimum coverage**: 80% line coverage
- **Critical paths**: 95% coverage for core image generation logic
- **New features**: Must include comprehensive tests

### Writing Tests

```kotlin
@Test
fun `should generate image with correct dimensions`() {
    // Given
    val request = SnapRequest(
        code = "println(\"Hello\")",
        language = "kotlin",
        designSystem = "material"
    )
    
    // When
    val result = snapService.generateImage(request)
    
    // Then
    assertThat(result.width).isGreaterThan(0)
    assertThat(result.height).isGreaterThan(0)
}
```

## Submitting Changes

### Pull Request Process

1. **Create Pull Request**
   - Use descriptive title following commit convention
   - Fill out PR template completely
   - Link related issues with `Fixes #123`

2. **PR Requirements**
   - ✅ All CI checks passing
   - ✅ Code review approval
   - ✅ Up-to-date with main branch
   - ✅ No merge conflicts

3. **Review Process**
   - Maintainer review required
   - Address all feedback comments
   - Resolve conversations before merge

### PR Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] Tests added/updated
```

### Merge Requirements

Before merging, ensure:
- ✅ All required status checks pass
- ✅ At least one approving review
- ✅ All conversations resolved
- ✅ Branch is up-to-date with main
- ✅ No conflicts exist

## Release Process

### Version Strategy

We use [Semantic Versioning](https://semver.org/):
- **Major** (X.0.0): Breaking changes
- **Minor** (0.X.0): New features, backward compatible
- **Patch** (0.0.X): Bug fixes, backward compatible

### Release Types

1. **Development Releases** (0.x.x): Active development
2. **Stable Releases** (1.x.x): Production ready
3. **Hotfix Releases** (x.x.X): Critical bug fixes

### Release Workflow

1. **Prepare Release**
   ```bash
   # Update CHANGELOG.md
   # Update version in build.gradle.kts
   git commit -m "chore: prepare release v1.0.0"
   ```

2. **Create Release**
   ```bash
   git tag -a v1.0.0 -m "Release v1.0.0"
   git push origin v1.0.0
   ```

3. **Automated Process**
   - GitHub Actions builds and tests
   - Docker image published to ghcr.io
   - GitHub Release created with artifacts
   - JAR files attached to release

### Release Artifacts

Each release includes:
- 📦 **JAR files**: Executable application
- 🐳 **Docker images**: Container deployment
- 📝 **Release notes**: Generated from CHANGELOG.md
- 🔍 **Build reports**: Test coverage and quality metrics

## Additional Resources

### Documentation
- [README.md](README.md): Project overview and quick start
- [CONTEXT.md](CONTEXT.md): Technical implementation details
- [CHANGELOG.md](CHANGELOG.md): Release history and changes
- [API Documentation](docs/): Detailed API reference

### Getting Help
- 🐛 **Bug Reports**: Create an issue with bug template
- 💡 **Feature Requests**: Create an issue with feature template
- ❓ **Questions**: Start a discussion in GitHub Discussions
- 💬 **Community**: Join our community discussions

### Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/). Please read and follow these guidelines to ensure a welcoming environment for all contributors.

---

Thank you for contributing to Code Snapper API! 🚀