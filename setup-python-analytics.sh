#!/bin/bash

echo "Setting up Python Analytics Environment for AirSight..."
echo

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    if ! command -v python &> /dev/null; then
        echo "Python is not installed or not in PATH."
        echo "Please install Python 3.8+ and add it to your PATH."
        exit 1
    else
        PYTHON_CMD="python"
    fi
else
    PYTHON_CMD="python3"
fi

echo "Python is installed. Installing required packages..."
echo

# Install Python packages
$PYTHON_CMD -m pip install -r python-requirements.txt

if [ $? -ne 0 ]; then
    echo "Failed to install Python packages."
    echo "Please ensure pip is working and you have internet connection."
    exit 1
fi

echo
echo "Python analytics environment setup complete!"
echo "You can now use the enhanced PDF reports and analytics features."
echo
