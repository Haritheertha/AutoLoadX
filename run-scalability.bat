@echo off
echo === AutoLoadX Scalability Testing Strategy ===
echo Comprehensive load testing with performance analysis
echo.

cd /d "C:\Users\HP\Load_Testing"

if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

echo Compiling scalability testing components...
javac -d build\classes src\main\java\com\loadtesting\phase1\model\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\service\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\ScalabilityTestApp.java

if %errorlevel% equ 0 (
    echo.
    echo Scalability tester compiled successfully!
    echo Starting comprehensive load testing...
    echo.
    java -cp build\classes com.loadtesting.phase1.ScalabilityTestApp
) else (
    echo.
    echo Compilation failed. Check errors above.
)

pause