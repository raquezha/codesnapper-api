#!/bin/bash

# 🧪 Code Snapper Test Automation Script
# This script starts the server and runs all design system tests

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVER_PORT=8081
SERVER_URL="http://127.0.0.1:$SERVER_PORT"
TEST_DIR="docs/testing"
OUTPUT_DIR="$TEST_DIR/generated"
SERVER_PID=""

# Create output directory
mkdir -p "$OUTPUT_DIR"

echo -e "${BLUE}🚀 Starting Code Snapper Test Automation${NC}"
echo "=========================================="

# Function to cleanup on exit
cleanup() {
    if [ ! -z "$SERVER_PID" ]; then
        echo -e "${YELLOW}🛑 Stopping server (PID: $SERVER_PID)${NC}"
        kill $SERVER_PID 2>/dev/null || true
        wait $SERVER_PID 2>/dev/null || true
    fi
}

# Set trap to cleanup on script exit
trap cleanup EXIT

# Function to check if server is ready
wait_for_server() {
    echo -e "${YELLOW}⏳ Waiting for server to start...${NC}"
    for i in {1..30}; do
        if curl -s "$SERVER_URL/health" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ Server is ready!${NC}"
            return 0
        fi
        sleep 1
        echo -n "."
    done
    echo -e "${RED}❌ Server failed to start within 30 seconds${NC}"
    exit 1
}

# Function to run a single test
run_test() {
    local test_file="$1"
    local test_name=$(basename "$test_file" .json)

    echo -e "${BLUE}🧪 Running test: $test_name${NC}"

    # Extract design system from filename
    if [[ "$test_name" == *"_macos"* ]]; then
        design_system="macOS"
    elif [[ "$test_name" == *"_material"* ]]; then
        design_system="Material Design"
    else
        design_system="Unknown"
    fi

    # Make API call and save result to temp file
    local output_file="$OUTPUT_DIR/${test_name}.png"
    local tmp_output_file="${output_file}.tmp"
    local headers_file="${output_file}.headers"

    curl -X POST "$SERVER_URL/snap" \
        -H "Content-Type: application/json" \
        -d @"$test_file" \
        --silent --show-error \
        --output "$tmp_output_file" \
        --dump-header "$headers_file" > /dev/null

    local http_status=$(awk 'NR==1{print $2}' "$headers_file")
    local content_type=$(awk -F': ' '/^Content-Type:/ {print $2}' "$headers_file" | tr -d '\r')

    if [[ "$http_status" == "200" && "$content_type" == "image/png" ]]; then
        mv "$tmp_output_file" "$output_file"
        rm -f "$headers_file"
        local file_size=$(du -h "$output_file" | cut -f1)
        echo -e "${GREEN}✅ $test_name ($design_system) - Generated: $file_size${NC}"
    else
        echo -e "${RED}❌ $test_name ($design_system) - API call failed or did not return PNG (status: $http_status, content-type: $content_type)${NC}"
        if [ -f "$tmp_output_file" ]; then
            echo -e "${YELLOW}--- Response Body ---${NC}"
            head -20 "$tmp_output_file"
            echo -e "${YELLOW}---------------------${NC}"
            rm -f "$tmp_output_file"
        fi
        rm -f "$headers_file"
        return 1
    fi
}

# Start the server in background
echo -e "${YELLOW}🚀 Starting Code Snapper server...${NC}"
./gradlew run &
SERVER_PID=$!

# Wait for server to be ready
wait_for_server

# Run all tests
echo -e "${BLUE}🧪 Running all design system tests...${NC}"
echo "=========================================="

# Test counters
total_tests=0
passed_tests=0
failed_tests=0

# Find and run all test files
for test_file in "$TEST_DIR"/test_*_lines_*.json; do
    if [ -f "$test_file" ]; then
        total_tests=$((total_tests + 1))
        if run_test "$test_file"; then
            passed_tests=$((passed_tests + 1))
        else
            failed_tests=$((failed_tests + 1))
        fi
        echo ""
    fi
done

# Summary
echo "=========================================="
echo -e "${BLUE}📊 Test Results Summary${NC}"
echo "Total tests: $total_tests"
echo -e "Passed: ${GREEN}$passed_tests${NC}"
echo -e "Failed: ${RED}$failed_tests${NC}"

if [ $failed_tests -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed! Images generated in $OUTPUT_DIR${NC}"
else
    echo -e "${RED}❌ Some tests failed. Check the output above for details.${NC}"
    exit 1
fi

echo -e "${BLUE}📁 Generated files:${NC}"
ls -la "$OUTPUT_DIR"/*.png 2>/dev/null || echo "No PNG files generated"
