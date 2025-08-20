@echo off
echo Testing Maven compilation...
echo.

echo Cleaning project...
call mvn clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven clean failed
    exit /b 1
)

echo.
echo Compiling project...
call mvn compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven compile failed
    exit /b 1
)

echo.
echo SUCCESS: Project compiled successfully!
