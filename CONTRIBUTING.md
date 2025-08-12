# Contributing to Code Snapper API

## 🚀 Feature Development Workflow

This project follows industry-standard development practices with GitHub Actions CI/CD integration.

### 1. Feature Planning & Documentation

Before starting any feature development:

1. **Update CHANGELOG.md** - Move feature from "Planned Features" to "In Progress"
2. **Create/Update Documentation** - Document the feature in README.md and CONTEXT.md
3. **Define Acceptance Criteria** - Clear, testable requirements

### 2. Branch Strategy

We follow **Git Flow** with conventional naming:

```bash
# Feature branches
git checkout -b feature/add-batch-processing
git checkout -b feature/rate-limiting
git checkout -b feature/download-urls

# Bug fixes
git checkout -b fix/image-rendering-issue

# Documentation updates
git checkout -b docs/api-documentation

# Refactoring
git checkout -b refactor/validation-service
```

### 3. Development Process

#### Step 1: Create Branch
```bash
git checkout main
git pull origin main
git checkout -b feature/your-feature-name
```

#### Step 2: Implement Feature
- Write failing tests first (TDD approach)
- Implement the feature
- Ensure all tests pass
- Run code quality checks

#### Step 3: Quality Assurance
```bash
# Run all quality checks
./gradlew test
./gradlew ktlintCheck
./gradlew detekt
./gradlew jacocoTestReport

# Run automation tests
./test-automation.sh
./test-material-design.sh
```

#### Step 4: Commit Changes
Follow **Conventional Commits** specification:

```bash
# Feature commits
git commit -m "feat: add batch processing endpoint for multiple code snippets"
git commit -m "feat(api): implement rate limiting with Redis backend"

# Bug fixes
git commit -m "fix: resolve image rendering issue with long code lines"

# Documentation
git commit -m "docs: add API examples for new batch endpoint"

# Tests
git commit -m "test: add integration tests for rate limiting"

# Refactoring
git commit -m "refactor: extract validation logic to separate service"

# Breaking changes
git commit -m "feat!: change API response format for better error handling"
```

### 4. Pull Request Process

#### Step 1: Push Branch
```bash
git push origin feature/your-feature-name
```

#### Step 2: Create Pull Request
- Use descriptive title following conventional commits
- Include comprehensive description
- Link to related issues
- Add screenshots/examples if applicable

#### Step 3: PR Template
```markdown
## 🚀 Feature Description
Brief description of what this PR accomplishes.

## 🧪 Testing
- [ ] Unit tests added/updated
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Code quality checks pass

## 📖 Documentation
- [ ] README.md updated (if needed)
- [ ] CHANGELOG.md updated
- [ ] API documentation updated (if needed)

## 🔗 Related Issues
Closes #123

## 📷 Screenshots/Examples
(If applicable)
```

#### Step 4: Code Review Process
- At least 1 approval required
- All CI checks must pass
- Address review feedback
- Squash commits if needed

### 5. Merge & Deployment

#### Merge Strategy
- **Squash and merge** for feature branches
- Use conventional commit message for merge commit
- Delete branch after merge

#### Post-Merge
- Update local main branch
- Verify deployment (if auto-deployed)
- Monitor for any issues

## 🧪 Testing Standards

### Test Categories
1. **Unit Tests** - Individual component testing
2. **Integration Tests** - API endpoint testing
3. **Contract Tests** - API response validation
4. **Visual Tests** - Image output validation

### Test Coverage Requirements
- Minimum 80% code coverage
- All new features must have tests
- All bug fixes must have regression tests

## 📋 Code Quality Standards

### Automated Checks
- **ktlint** - Code formatting
- **detekt** - Static analysis
- **JaCoCo** - Test coverage
- **GitHub Actions** - CI/CD pipeline

### Manual Review Checklist
- [ ] Code follows project conventions
- [ ] Performance considerations addressed
- [ ] Security implications reviewed
- [ ] Error handling implemented
- [ ] Logging added where appropriate

## 🏷️ Release Process

### Version Numbering
Follow [Semantic Versioning](https://semver.org/):
- **MAJOR** - Breaking changes
- **MINOR** - New features (backward compatible)
- **PATCH** - Bug fixes

### Release Steps
1. Update version in `gradle.properties`
2. Update CHANGELOG.md
3. Create release tag
4. Deploy to production

## 🚨 Hotfix Process

For critical production issues:

```bash
git checkout main
git checkout -b hotfix/critical-bug-fix
# Fix the issue
git commit -m "fix: resolve critical production issue"
# Create PR with "hotfix" label
```

## 📞 Getting Help

- Create GitHub issue for bugs/features
- Use discussions for questions
- Review existing PRs for examples
