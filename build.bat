@echo off
chcp 65001

cd /d "C:\Users\ygs\Desktop\FocusBloom"

echo ===========================================
echo FocusBloom APK Builder
echo ===========================================
echo.
echo [1] Build Debug APK
echo [2] Build Release APK
echo [3] Exit
echo.
set /p choice="Select option: "

if "%choice%"=="1" goto debug
if "%choice%"=="2" goto release
if "%choice%"=="3" goto end

goto end

:debug
call gradlew.bat assembleDebug
if %errorlevel%==0 (
    echo.
    echo Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    explorer app\build\outputs\apk\debug
) else (
    echo Build failed!
)
pause
goto end

:release
call gradlew.bat assembleRelease
if %errorlevel%==0 (
    echo.
    echo Build successful!
    echo APK location: app\build\outputs\apk\release\app-release-unsigned.apk
    explorer app\build\outputs\apk\release
) else (
    echo Build failed!
)
pause
goto end

:end
echo Goodbye!