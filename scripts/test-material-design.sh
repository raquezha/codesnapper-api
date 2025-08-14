#!/bin/bash

# Material Design Testing Script
# Runs Material Design compliance tests and generates reports

set -e

echo "🎨 Material Design Compliance Testing"
echo "====================================="

# Check if we're in the right directory
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Create output directory
mkdir -p docs/testing/generated

echo "📦 Compiling project..."
./gradlew compileKotlin

echo "🧪 Running Material Design tests..."
./gradlew runMaterialDesignTests

echo "✅ Material Design compliance testing completed!"
echo "📋 Check docs/testing/material_design_compliance_report.txt for detailed results"
echo "🖼️  Generated test images are in docs/testing/generated/"
