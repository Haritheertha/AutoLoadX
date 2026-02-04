@echo off
echo === AutoLoadX - Performance Testing Tool ===
echo Building and running Phase 1...
echo.

cd /d "C:\Users\HP\Load_Testing"

if not exist "build" mkdir build
if not exist "build\classes" mkdir build\classes

echo Compiling Phase 1 classes...
javac -d build\classes src\main\java\com\loadtesting\phase1\model\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\service\*.java
javac -cp build\classes -d build\classes src\main\java\com\loadtesting\phase1\Phase1App.java

if %errorlevel% equ 0 (
    echo.
    echo Phase 1 compiled successfully!
    echo Starting load testing tool...
    echo.
    java -cp build\classes com.loadtesting.phase1.Phase1App
) else (
    echo.
    echo Compilation failed. Check errors above.
)

pause