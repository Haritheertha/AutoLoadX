@echo off
echo === AutoLoadX High-Load Testing (Memory Optimized) ===
echo.

cd /d "C:\Users\HP\Load_Testing"

if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

echo Compiling with memory optimizations...
javac -d build\classes src\main\java\com\loadtesting\phase1\model\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\service\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\ScalabilityTestApp.java

if %errorlevel% equ 0 (
    echo.
    echo Starting high-load testing with memory optimization...
    echo JVM Settings: 4GB Heap, Optimized GC
    echo.
    java -Xmx4g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -cp build\classes com.loadtesting.phase1.ScalabilityTestApp
) else (
    echo Compilation failed.
)

pause