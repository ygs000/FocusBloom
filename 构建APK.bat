@echo off
chcp 65001 >nul
echo ======================================
echo FocusBloom 本地构建工具
echo ======================================
echo.

cd /d "%~dp0"

echo 当前目录: %CD%
echo.

if not exist "gradlew.bat" (
    echo 错误: 找不到 gradlew.bat 文件！
    echo 请确保此脚本放在 FocusBloom 项目根目录。
    pause
    exit /b 1
)

echo 请选择构建类型:
echo   [1] Debug APK (调试版本，推荐测试用)
echo   [2] Release APK (发布版本，体积更小)
echo   [3] 退出
echo.

set /p choice="请输入选项 (1/2/3): "

if "%choice%"=="1" goto build_debug
if "%choice%"=="2" goto build_release
if "%choice%"=="3" goto exit
goto invalid

:build_debug
echo.
echo ======================================
echo 正在构建 Debug APK...
echo ======================================
echo.
call gradlew.bat assembleDebug --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo 构建失败！请检查错误信息。
    pause
    exit /b 1
)
echo.
echo ======================================
echo Debug APK 构建成功！
echo ======================================
echo.
echo APK 文件位置:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.
explorer "app\build\outputs\apk\debug"
pause
goto exit

:build_release
echo.
echo ======================================
echo 正在构建 Release APK...
echo ======================================
echo.
call gradlew.bat assembleRelease --no-daemon
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo 构建失败！请检查错误信息。
    pause
    exit /b 1
)
echo.
echo ======================================
echo Release APK 构建成功！
echo ======================================
echo.
echo APK 文件位置:
echo   app\build\outputs\apk\release\app-release-unsigned.apk
echo.
explorer "app\build\outputs\apk\release"
pause
goto exit

:invalid
echo.
echo 无效的选项，请重新运行脚本。
pause
exit /b 1

:exit
echo.
echo 按任意键退出...
pause >nul