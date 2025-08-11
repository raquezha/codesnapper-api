# 🤖 Automation Setup Guide

This guide explains how to use the automation features in Code Snapper.

## ✅ What's Already Configured

All automation is now ready to use! Here's what was implemented:

### 1. Pre-commit Hooks
- **Location**: `.git/hooks/pre-commit`
- **What it does**: Runs before every commit to ensure code quality
- **Includes**: ktlint formatting, detekt analysis, and tests
- **Status**: ✅ Active (executable script created)

### 2. GitHub Actions CI/CD
- **Location**: `.github/workflows/ci.yml`
- **Triggers**: Every push to main/develop, every PR to main
- **Stages**: Code quality → Tests → Build
- **Artifacts**: Test reports, coverage reports, build JARs
- **Status**: ✅ Ready (will activate on next push to GitHub)

### 3. Format on Save (IntelliJ IDEA)
- **Location**: `.idea/workspace.xml`
- **What it does**: Auto-formats Kotlin files when you save
- **Includes**: Code formatting and import optimization
- **Status**: ✅ Configured (active when you open project in IntelliJ)

### 4. EditorConfig
- **Location**: `.editorconfig`
- **What it does**: Consistent formatting across editors
- **Supports**: IntelliJ IDEA, VS Code, and other editors
- **Status**: ✅ Active

## 🚀 How to Use

### Testing Pre-commit Hooks
```bash
# Make any change to a file
echo "// test" >> src/main/kotlin/net/raquezha/codesnapper/Application.kt

# Try to commit - hooks will run automatically
git add .
git commit -m "test: verify pre-commit hooks"
```

### Manual Code Quality Checks
```bash
# Run all quality checks manually
./gradlew codeQuality

# Or run individual tools
./gradlew ktlintFormat  # Auto-fix formatting
./gradlew detekt        # Static analysis
./gradlew test          # Run tests
```

### CI/CD Pipeline
- Push to GitHub - pipeline runs automatically
- Create PR - pipeline validates your changes
- Check "Actions" tab on GitHub to see results

## 📊 Reports Location

All quality reports are generated in:
```
build/reports/
├── detekt/          # Static analysis reports
├── ktlint/          # Code style reports
├── jacoco/          # Code coverage reports
└── tests/           # Test execution reports
```

## 🔮 Future Automation (Coming Later)

These features are planned for future implementation:
- **Dependency Updates**: Automated PRs for dependency updates
- **Security Scanning**: Vulnerability scanning
- **Release Automation**: Semantic versioning and releases
- **Documentation Generation**: API docs and coverage badges
- **Performance Monitoring**: Benchmark tests

## 🛠️ Troubleshooting

### Pre-commit Hook Issues
```bash
# If hooks don't run, check permissions
ls -la .git/hooks/pre-commit
# Should show: -rwxr-xr-x (executable)

# Fix permissions if needed
chmod +x .git/hooks/pre-commit
```

### IDE Configuration
- Open project in IntelliJ IDEA
- Format-on-save should activate automatically
- Check: Settings → Tools → Actions on Save
