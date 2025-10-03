@echo off
chcp 65001 >nul 2>&1
title BS Interpreter Manager
setlocal EnableExtensions EnableDelayedExpansion

:: ============================================
::  BS (Bitwise Subleq) Interpreter Manager
::  Author: MCLMLI
::  Version: 1.0.0
:: ============================================

:: ----------- Configuration -----------
set "INSTALL_DIR=%ProgramFiles%\BS\Interpreter"
set "BAT_NAME=bs.bat"
set "JAR_NAME=Bitwise-Subleq-Interpreter-Java-1.0-SNAPSHOT.jar"
set "REG_KEY=HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment"
set "LANG=zh"

:: ----------- Check Administrator -----------
fltmc >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Administrator privileges required!
    echo Please right-click and select "Run as administrator"
    echo.
    echo [错误] 需要管理员权限！
    echo 请右键选择"以管理员身份运行"
    pause & exit /b 1
)

:: ----------- Language Selection -----------
:lang_menu
cls
echo.
echo    ========================================
echo       BS Interpreter Manager / 管理器
echo    ========================================
echo.
echo    Please select language / 请选择语言：
echo.
echo    [1] English
echo    [2] 中文
echo    [Q] Quit / 退出
echo.
set /p lang_choice= Your choice / 您的选择：
if /i "%lang_choice%"=="1" set "LANG=en" & goto :main_menu
if /i "%lang_choice%"=="2" set "LANG=zh" & goto :main_menu
if /i "%lang_choice%"=="Q" exit /b 0
goto :lang_menu

:: ----------- Main Menu -----------
:main_menu
cls
if "%LANG%"=="zh" goto :menu_zh
goto :menu_en

:menu_zh
echo.
echo    ========================================
echo         BS (Bitwise Subleq) 解释器管理器
echo    ========================================
echo.
echo    [1] 安装 BS 解释器
echo    [2] 更新 BS 解释器
echo    [3] 卸载 BS 解释器
echo    [4] 查看安装状态
echo    [5] 测试解释器
echo    [6] Switch Language
echo    [Q] 退出
echo.
set /p choice= 请选择 (1-6/Q)：
if "%choice%"=="1" goto :install
if "%choice%"=="2" goto :update
if "%choice%"=="3" goto :uninstall
if "%choice%"=="4" goto :status
if "%choice%"=="5" goto :test
if /i "%choice%"=="6" goto :lang_menu
if /i "%choice%"=="Q" exit /b 0
goto :main_menu

:menu_en
echo.
echo    ========================================
echo       BS (Bitwise Subleq) Interpreter Manager
echo    ========================================
echo.
echo    [1] Install BS Interpreter
echo    [2] Update BS Interpreter
echo    [3] Uninstall BS Interpreter
echo    [4] View Installation Status
echo    [5] Test Interpreter
echo    [6] 切换语言
echo    [Q] Quit
echo.
set /p choice= Your choice (1-6/Q):
if "%choice%"=="1" goto :install
if "%choice%"=="2" goto :update
if "%choice%"=="3" goto :uninstall
if "%choice%"=="4" goto :status
if /i "%choice%"=="5" goto :test
if /i "%choice%"=="6" goto :lang_menu
if /i "%choice%"=="Q" exit /b 0
goto :main_menu

:: ============================================
:: Installation
:: ============================================
:install
cls
if "%LANG%"=="zh" (
    echo.
    echo [安装 BS 解释器]
    echo ========================================
    echo.
) else (
    echo.
    echo [Install BS Interpreter]
    echo ========================================
    echo.
)

:: Check if already installed
if exist "%INSTALL_DIR%\%JAR_NAME%" (
    if "%LANG%"=="zh" (
        echo BS 解释器已经安装。
        echo 如需更新，请选择菜单中的"更新"选项。
    ) else (
        echo BS Interpreter is already installed.
        echo Please use "Update" option to update.
    )
    pause & goto :main_menu
)

:: Find source files
call :find_jar
if "%JAR_PATH%"=="" (
    if "%LANG%"=="zh" (
        echo [错误] 找不到 JAR 文件！
        echo 请确保在正确的目录运行此脚本，或先运行 gradlew build
    ) else (
        echo [ERROR] JAR file not found!
        echo Please run this script in the correct directory or build first.
    )
    pause & goto :main_menu
)

if not exist "%~dp0%BAT_NAME%" (
    if "%LANG%"=="zh" (
        echo [错误] 找不到 %BAT_NAME%！
    ) else (
        echo [ERROR] %BAT_NAME% not found!
    )
    pause & goto :main_menu
)

:: Create directory
if "%LANG%"=="zh" (
    echo 正在创建安装目录...
) else (
    echo Creating installation directory...
)
if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"

:: Copy files
if "%LANG%"=="zh" (
    echo 正在复制文件...
) else (
    echo Copying files...
)
copy /Y "%~dp0%BAT_NAME%" "%INSTALL_DIR%\%BAT_NAME%" >nul
copy /Y "%JAR_PATH%" "%INSTALL_DIR%\%JAR_NAME%" >nul

:: Add to PATH
if "%LANG%"=="zh" (
    echo 正在添加到系统环境变量...
) else (
    echo Adding to system PATH...
)
call :add_to_path

if "%LANG%"=="zh" (
    echo.
    echo ✓ 安装完成！
    echo.
    echo 使用方法：
    echo   1. 重新打开命令提示符窗口
    echo   2. 输入 bs --help 查看帮助
    echo   3. 输入 bs -e 000000000000000 测试运行
    echo.
    echo 安装路径: %INSTALL_DIR%
) else (
    echo.
    echo ✓ Installation completed!
    echo.
    echo Usage:
    echo   1. Reopen command prompt
    echo   2. Type: bs --help
    echo   3. Type: bs -e 000000000000000 to test
    echo.
    echo Install path: %INSTALL_DIR%
)
pause & goto :main_menu

:: ============================================
:: Update
:: ============================================
:update
cls
if "%LANG%"=="zh" (
    echo.
    echo [更新 BS 解释器]
    echo ========================================
    echo.
) else (
    echo.
    echo [Update BS Interpreter]
    echo ========================================
    echo.
)

:: Check if installed
if not exist "%INSTALL_DIR%\%JAR_NAME%" (
    if "%LANG%"=="zh" (
        echo BS 解释器尚未安装。
        echo 请先使用"安装"选项。
    ) else (
        echo BS Interpreter is not installed.
        echo Please install first.
    )
    pause & goto :main_menu
)

:: Find source files
call :find_jar
if "%JAR_PATH%"=="" (
    if "%LANG%"=="zh" (
        echo [错误] 找不到新版本 JAR 文件！
    ) else (
        echo [ERROR] New JAR file not found!
    )
    pause & goto :main_menu
)

:: Backup old version
if "%LANG%"=="zh" (
    echo 正在备份旧版本...
) else (
    echo Backing up old version...
)
copy /Y "%INSTALL_DIR%\%JAR_NAME%" "%INSTALL_DIR%\%JAR_NAME%.bak" >nul

:: Copy new files
if "%LANG%"=="zh" (
    echo 正在更新文件...
) else (
    echo Updating files...
)
copy /Y "%JAR_PATH%" "%INSTALL_DIR%\%JAR_NAME%" >nul
if exist "%~dp0%BAT_NAME%" copy /Y "%~dp0%BAT_NAME%" "%INSTALL_DIR%\%BAT_NAME%" >nul

if "%LANG%"=="zh" (
    echo.
    echo ✓ 更新完成！
    echo.
    echo 备份文件: %JAR_NAME%.bak
) else (
    echo.
    echo ✓ Update completed!
    echo.
    echo Backup file: %JAR_NAME%.bak
)
pause & goto :main_menu

:: ============================================
:: Uninstall
:: ============================================
:uninstall
cls
if "%LANG%"=="zh" (
    echo.
    echo [卸载 BS 解释器]
    echo ========================================
    echo.
    echo 警告：这将删除 BS 解释器的所有文件！
    echo.
    set /p confirm=确定要卸载吗？(Y/N):
) else (
    echo.
    echo [Uninstall BS Interpreter]
    echo ========================================
    echo.
    echo Warning: This will remove all BS Interpreter files!
    echo.
    set /p confirm=Are you sure? (Y/N):
)

if /i not "%confirm%"=="Y" goto :main_menu

:: Remove from PATH
if "%LANG%"=="zh" (
    echo 正在从环境变量中移除...
) else (
    echo Removing from PATH...
)
call :remove_from_path

:: Delete directory
if "%LANG%"=="zh" (
    echo 正在删除文件...
) else (
    echo Deleting files...
)
if exist "%INSTALL_DIR%" rd /s /q "%INSTALL_DIR%"

:: Delete parent directory if empty
rd "%ProgramFiles%\BS" 2>nul

if "%LANG%"=="zh" (
    echo.
    echo ✓ 卸载完成！
) else (
    echo.
    echo ✓ Uninstall completed!
)
pause & goto :main_menu

:: ============================================
:: Status
:: ============================================
:status
cls
if "%LANG%"=="zh" (
    echo.
    echo [安装状态]
    echo ========================================
    echo.
) else (
    echo.
    echo [Installation Status]
    echo ========================================
    echo.
)

if exist "%INSTALL_DIR%\%JAR_NAME%" (
    if "%LANG%"=="zh" (
        echo 状态: ✓ 已安装
        echo 安装路径: %INSTALL_DIR%
        echo.
        echo 文件列表:
    ) else (
        echo Status: ✓ Installed
        echo Install path: %INSTALL_DIR%
        echo.
        echo Files:
    )
    dir /b "%INSTALL_DIR%"
    echo.

    :: Check if in PATH
    for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "CURRENT_PATH=%%B"
    echo ;!CURRENT_PATH!; | find /i ";%INSTALL_DIR%;" >nul
    if errorlevel 1 (
        if "%LANG%"=="zh" (
            echo 环境变量: ✗ 未配置
        ) else (
            echo PATH: ✗ Not configured
        )
    ) else (
        if "%LANG%"=="zh" (
            echo 环境变量: ✓ 已配置
        ) else (
            echo PATH: ✓ Configured
        )
    )

    :: Get file info
    echo.
    if "%LANG%"=="zh" (
        echo JAR 文件信息:
    ) else (
        echo JAR file info:
    )
    for %%F in ("%INSTALL_DIR%\%JAR_NAME%") do (
        if "%LANG%"=="zh" (
            echo   大小: %%~zF 字节
            echo   修改时间: %%~tF
        ) else (
            echo   Size: %%~zF bytes
            echo   Modified: %%~tF
        )
    )
) else (
    if "%LANG%"=="zh" (
        echo 状态: ✗ 未安装
        echo.
        echo 使用"安装"选项来安装 BS 解释器。
    ) else (
        echo Status: ✗ Not installed
        echo.
        echo Use "Install" option to install BS Interpreter.
    )
)
echo.
pause & goto :main_menu

:: ============================================
:: Test
:: ============================================
:test
cls
if "%LANG%"=="zh" (
    echo.
    echo [测试 BS 解释器]
    echo ========================================
    echo.
) else (
    echo.
    echo [Test BS Interpreter]
    echo ========================================
    echo.
)

if not exist "%INSTALL_DIR%\%JAR_NAME%" (
    if "%LANG%"=="zh" (
        echo BS 解释器尚未安装。
    ) else (
        echo BS Interpreter is not installed.
    )
    pause & goto :main_menu
)

if "%LANG%"=="zh" (
    echo 测试 1: 显示帮助信息
    echo ----------------------------------------
) else (
    echo Test 1: Display help
    echo ----------------------------------------
)
java -jar "%INSTALL_DIR%\%JAR_NAME%" --help
echo.
echo.

if "%LANG%"=="zh" (
    echo 测试 2: 执行无限循环程序（自动停止）
    echo ----------------------------------------
) else (
    echo Test 2: Execute infinite loop (auto-stop)
    echo ----------------------------------------
)
java -jar "%INSTALL_DIR%\%JAR_NAME%" -e 000000000000000 --lang %LANG%
echo.
echo.

if "%LANG%"=="zh" (
    echo ✓ 测试完成！
    echo.
    echo 如果上述测试正常运行，说明 BS 解释器已正确安装。
    echo 您可以在任何位置使用 bs 命令。
) else (
    echo ✓ Test completed!
    echo.
    echo If the tests ran successfully, BS Interpreter is correctly installed.
    echo You can use bs command from anywhere.
)
echo.
pause & goto :main_menu

:: ============================================
:: Helper Functions
:: ============================================

:find_jar
set "JAR_PATH="
if exist "%~dp0build\libs\%JAR_NAME%" (
    set "JAR_PATH=%~dp0build\libs\%JAR_NAME%"
) else if exist "%~dp0%JAR_NAME%" (
    set "JAR_PATH=%~dp0%JAR_NAME%"
)
exit /b

:add_to_path
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_PATH=%%B"
echo ;%OLD_PATH%; | find /i ";%INSTALL_DIR%;" >nul
if errorlevel 1 (
    set "NEW_PATH=%OLD_PATH%;%INSTALL_DIR%"
    setx PATH "!NEW_PATH!" /M >nul 2>&1
)
exit /b

:remove_from_path
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_PATH=%%B"
set "NEW_PATH=!OLD_PATH:;%INSTALL_DIR%=!"
if not "!OLD_PATH!"=="!NEW_PATH!" (
    setx PATH "!NEW_PATH!" /M >nul 2>&1
)
exit /b

