 # 🧪 Testing

This directory contains all the necessary files and documentation for testing the Code Snapper API. Our testing strategy includes automated scripts for generating images and a comprehensive framework for validating Material Design 3 compliance.

## 📂 Directory Structure

-   `docs/testing/`
    -   `generated/`: Contains output images from test runs for visual verification.
    -   `*.json`: Test case files with different code snippets and configurations.
    -   `material_design_compliance_report.txt`: A detailed report of the latest Material Design compliance check.
    -   `README.md`: This file.

## 🤖 Automated Testing

The project includes a script to automate the process of running tests for both **macOS** and **Material Design** systems.

### How to Run Tests

To run the full test suite, execute the following command from the project root:

```bash
./test-automation.sh
```

This script will:
1.  Start the Code Snapper Ktor server.
2.  Run all test cases defined in the `*.json` files.
3.  Generate output images and save them to the `docs/testing/generated/` directory.
4.  Provide a summary of pass/fail results in the console.

## 🎨 Material Design Compliance Testing

We have a dedicated framework to ensure our Material Design renderer is **100% compliant** with the official Google specifications.

### How to Run Compliance Validation

To run the Material Design compliance tests, execute the following command from the project root:

```bash
./test-material-design.sh
```

This script validates:
-   **8dp Grid System**: Checks for consistent spacing and alignment.
-   **Typography**: Verifies the use of the Roboto font and correct type scale.
-   **Color Contrast**: Ensures all text meets WCAG AA accessibility standards.
-   **Corner Radius**: Confirms standard Material Design corner radii are used.

The results are saved in `docs/testing/material_design_compliance_report.txt`, with a final compliance score.

## 📄 Test Cases

Test cases are defined in JSON files and are categorized by line count and design system.

-   `test_5_lines_*.json`: Short code snippets.
-   `test_15_lines_*.json`: Medium-length code examples.
-   `test_30_lines_*.json`: Longer code blocks to test wrapping and layout.

Each file contains a JSON payload that matches the `/snap` API endpoint, allowing for easy and repeatable tests.

