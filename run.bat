@echo off
cd /d "%~dp0"
set "LIBRARY_JAVAC=javac"
set "LIBRARY_JAVA=java"
where javac >nul 2>nul
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe" (
        set "LIBRARY_JAVAC=C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe"
        set "LIBRARY_JAVA=C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"
    )
)
if not exist out mkdir out
"%LIBRARY_JAVAC%" -encoding UTF-8 -source 8 -target 8 -d out src\*.java
if errorlevel 1 (
    echo Compilation failed. Check your JDK installation.
    pause
    exit /b 1
)
if /i "%~1"=="test" (
    "%LIBRARY_JAVA%" -cp out SelfTest
) else (
    "%LIBRARY_JAVA%" -cp out Main
)
pause
