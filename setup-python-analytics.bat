@echo off
echo Setting up Python Analytics Environment for AirSight...
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo Python is not installed or not in PATH.
    echo Please install Python 3.8+ and add it to your PATH.
    pause
    exit /b 1
)

echo Python is installed. Installing required packages...
echo.

REM Install Python packages
pip install -r python-requirements.txt

if errorlevel 1 (
    echo Failed to install Python packages.
    echo Please ensure pip is working and you have internet connection.
    pause
    exit /b 1
)

echo.
echo Python analytics environment setup complete!
echo You can now use the enhanced PDF reports and analytics features.
echo.
pause
