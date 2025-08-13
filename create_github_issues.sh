#!/bin/bash

# Script to create missing GitHub issues from CONTEXT.md TODOs
# This avoids terminal quote escaping problems

echo "Creating missing GitHub issues..."

# Issue 1: Configuration preset API
gh issue create \
  --title "Add configuration preset API" \
  --body "Integrate existing ImageConfiguration preset methods into API workflow with new preset parameter. Add lightTheme, presentation, and compact presets. Update SnapRequest and validation." \
  --label "enhancement,ready-to-code"

# Issue 2: Unit and integration tests
gh issue create \
  --title "Add unit and integration tests for endpoints" \
  --body "Implement comprehensive test coverage for all API endpoints. Create unit tests for /snap endpoint, integration tests for full API flow, test both design systems. Target 80% code coverage." \
  --label "enhancement,ready-to-code"

# Issue 3: Download/preview URLs
gh issue create \
  --title "Add download and preview URL functionality" \
  --body "Implement image storage and retrieval via URLs for sharing. Add GET /images/{id} endpoint, preview functionality, unique IDs for generated images, cleanup policies." \
  --label "enhancement,ready-to-code"

# Issue 4: Rate limiting
gh issue create \
  --title "Implement rate limiting and usage tracking" \
  --body "Add rate limiting middleware and usage tracking to prevent API abuse. Implement IP-based and user-based limits, usage statistics, rate limit headers in responses." \
  --label "enhancement,ready-to-code"

# Issue 5: Frontend playground
gh issue create \
  --title "Build a frontend playground" \
  --body "Create web-based frontend interface for testing API interactively. Add code editor, language/theme selection, design system toggle, live preview, download functionality." \
  --label "enhancement,ready-to-code"

# Issue 6: Pastebin-like history
gh issue create \
  --title "Add pastebin-like history functionality" \
  --body "Implement pastebin-style history system for storing and organizing generated images. Add history API endpoints, search/filtering, expiration policies, gallery view." \
  --label "enhancement,ready-to-code"

echo "All GitHub issues created successfully!"
