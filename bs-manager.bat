@echo off
chcp 936 >nul 2>&1
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
if errorlevel 1 goto :no_admin
goto :admin_ok

:no_admin
echo.
echo ========================================
echo [ERROR] Administrator privileges required
echo Please right-click and select "Run as administrator"
echo.
echo [错误] 需要管理员权限
echo 请右键选择"以管理员身份运行"
echo ========================================
echo.
pause
exit /b 1

:admin_ok

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
goto :menu_process

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
goto :menu_process

:menu_process
if "%choice%"=="1" goto :install
if "%choice%"=="2" goto :update
if "%choice%"=="3" goto :uninstall
if "%choice%"=="4" goto :status
if "%choice%"=="5" goto :test
if /i "%choice%"=="6" goto :lang_menu
if /i "%choice%"=="Q" exit /b 0
goto :main_menu

:: ============================================
:: Installation
:: ============================================
:install
cls
if "%LANG%"=="zh" goto :install_header_zh
goto :install_header_en

:install_header_zh
echo.
echo [安装 BS 解释器]
echo ========================================
echo.
goto :install_check

:install_header_en
echo.
echo [Install BS Interpreter]
echo ========================================
echo.
goto :install_check

:install_check
if exist "%INSTALL_DIR%\%JAR_NAME%" goto :already_installed
call :find_jar
if "%JAR_PATH%"=="" goto :jar_not_found
if not exist "%~dp0%BAT_NAME%" goto :bat_not_found

if "%LANG%"=="zh" goto :install_creating_zh
echo Creating installation directory...
goto :install_creating_done
:install_creating_zh
echo 正在创建安装目录...
:install_creating_done

if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"

if "%LANG%"=="zh" goto :install_copying_zh
echo Copying files...
goto :install_copying_done
:install_copying_zh
echo 正在复制文件...
:install_copying_done

copy /Y "%~dp0%BAT_NAME%" "%INSTALL_DIR%\%BAT_NAME%" >nul
copy /Y "%JAR_PATH%" "%INSTALL_DIR%\%JAR_NAME%" >nul

if "%LANG%"=="zh" goto :install_adding_zh
echo Adding to system PATH...
goto :install_adding_done
:install_adding_zh
echo 正在添加到系统环境变量...
:install_adding_done

call :add_to_path

if "%LANG%"=="zh" goto :install_success_zh
goto :install_success_en

:already_installed
if "%LANG%"=="zh" goto :already_installed_zh
echo BS Interpreter is already installed.
echo Please use "Update" option to update.
pause
goto :main_menu
:already_installed_zh
echo BS 解释器已经安装。
echo 如需更新，请选择菜单中的"更新"选项。
pause
goto :main_menu

:jar_not_found
if "%LANG%"=="zh" goto :jar_not_found_zh
echo [ERROR] JAR file not found!
echo Please run this script in the correct directory or build first.
pause
goto :main_menu
:jar_not_found_zh
echo [错误] 找不到 JAR 文件！
echo 请确保在正确的目录运行此脚本，或先运行 gradlew build
pause
goto :main_menu

:bat_not_found
if "%LANG%"=="zh" goto :bat_not_found_zh
echo [ERROR] %BAT_NAME% not found!
pause
goto :main_menu
:bat_not_found_zh
echo [错误] 找不到 %BAT_NAME%！
pause
goto :main_menu

:install_success_zh
echo.
echo ? 安装完成！
echo.
echo 使用方法：
echo   1. 重新打开命令提示符窗口
echo   2. 输入 bs --help 查看帮助
echo   3. 输入 bs -e 000000000000000 测试运行
echo.
echo 安装路径: %INSTALL_DIR%
pause
goto :main_menu

:install_success_en
echo.
echo ? Installation completed!
echo.
echo Usage:
echo   1. Reopen command prompt
echo   2. Type: bs --help
echo   3. Type: bs -e 000000000000000 to test
echo.
echo Install path: %INSTALL_DIR%
pause
goto :main_menu

:: ============================================
:: Update
:: ============================================
:update
cls
if "%LANG%"=="zh" goto :update_header_zh
goto :update_header_en

:update_header_zh
echo.
echo [更新 BS 解释器]
echo ========================================
echo.
goto :update_check

:update_header_en
echo.
echo [Update BS Interpreter]
echo ========================================
echo.
goto :update_check

:update_check
if not exist "%INSTALL_DIR%\%JAR_NAME%" goto :not_installed
call :find_jar
if "%JAR_PATH%"=="" goto :new_jar_not_found

if "%LANG%"=="zh" goto :update_backing_zh
echo Backing up old version...
goto :update_backing_done
:update_backing_zh
echo 正在备份旧版本...
:update_backing_done

copy /Y "%INSTALL_DIR%\%JAR_NAME%" "%INSTALL_DIR%\%JAR_NAME%.bak" >nul

if "%LANG%"=="zh" goto :update_updating_zh
echo Updating files...
goto :update_updating_done
:update_updating_zh
echo 正在更新文件...
:update_updating_done

copy /Y "%JAR_PATH%" "%INSTALL_DIR%\%JAR_NAME%" >nul
if exist "%~dp0%BAT_NAME%" copy /Y "%~dp0%BAT_NAME%" "%INSTALL_DIR%\%BAT_NAME%" >nul

if "%LANG%"=="zh" goto :update_success_zh
goto :update_success_en

:not_installed
if "%LANG%"=="zh" goto :not_installed_zh
echo BS Interpreter is not installed.
echo Please install first.
pause
goto :main_menu
:not_installed_zh
echo BS 解释器尚未安装。
echo 请先使用"安装"选项。
pause
goto :main_menu

:new_jar_not_found
if "%LANG%"=="zh" goto :new_jar_not_found_zh
echo [ERROR] New JAR file not found!
pause
goto :main_menu
:new_jar_not_found_zh
echo [错误] 找不到新版本 JAR 文件！
pause
goto :main_menu

:update_success_zh
echo.
echo ? 更新完成！
echo.
echo 备份文件: %JAR_NAME%.bak
pause
goto :main_menu

:update_success_en
echo.
echo ? Update completed!
echo.
echo Backup file: %JAR_NAME%.bak
pause
goto :main_menu

:: ============================================
:: Uninstall
:: ============================================
:uninstall
cls
if "%LANG%"=="zh" goto :uninstall_header_zh
goto :uninstall_header_en

:uninstall_header_zh
echo.
echo [卸载 BS 解释器]
echo ========================================
echo.
echo 警告：这将删除 BS 解释器的所有文件！
echo.
set /p confirm=确定要卸载吗？(Y/N):
goto :uninstall_confirm

:uninstall_header_en
echo.
echo [Uninstall BS Interpreter]
echo ========================================
echo.
echo Warning: This will remove all BS Interpreter files!
echo.
set /p confirm=Are you sure? (Y/N):
goto :uninstall_confirm

:uninstall_confirm
if /i not "%confirm%"=="Y" goto :main_menu

if "%LANG%"=="zh" goto :uninstall_removing_zh
echo Removing from PATH...
goto :uninstall_removing_done
:uninstall_removing_zh
echo 正在从环境变量中移除...
:uninstall_removing_done

call :remove_from_path

if "%LANG%"=="zh" goto :uninstall_deleting_zh
echo Deleting files...
goto :uninstall_deleting_done
:uninstall_deleting_zh
echo 正在删除文件...
:uninstall_deleting_done

if exist "%INSTALL_DIR%" rd /s /q "%INSTALL_DIR%"
rd "%ProgramFiles%\BS" 2>nul

if "%LANG%"=="zh" goto :uninstall_success_zh
goto :uninstall_success_en

:uninstall_success_zh
echo.
echo ? 卸载完成！
pause
goto :main_menu

:uninstall_success_en
echo.
echo ? Uninstall completed!
pause
goto :main_menu

:: ============================================
:: Status
:: ============================================
:status
cls
if "%LANG%"=="zh" goto :status_header_zh
goto :status_header_en

:status_header_zh
echo.
echo [安装状态]
echo ========================================
echo.
goto :status_check

:status_header_en
echo.
echo [Installation Status]
echo ========================================
echo.
goto :status_check

:status_check
if exist "%INSTALL_DIR%\%JAR_NAME%" goto :status_installed
goto :status_not_installed

:status_installed
if "%LANG%"=="zh" goto :status_installed_zh
echo Status: ? Installed
echo Install path: %INSTALL_DIR%
echo.
echo Files:
goto :status_list_files
:status_installed_zh
echo 状态: ? 已安装
echo 安装路径: %INSTALL_DIR%
echo.
echo 文件列表:
:status_list_files

dir /b "%INSTALL_DIR%"
echo.

for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "CURRENT_PATH=%%B"
echo ;!CURRENT_PATH!; | find /i ";%INSTALL_DIR%;" >nul
if errorlevel 1 goto :status_path_not_configured
goto :status_path_configured

:status_path_not_configured
if "%LANG%"=="zh" goto :status_path_not_configured_zh
echo PATH: ? Not configured
goto :status_file_info
:status_path_not_configured_zh
echo 环境变量: ? 未配置
goto :status_file_info

:status_path_configured
if "%LANG%"=="zh" goto :status_path_configured_zh
echo PATH: ? Configured
goto :status_file_info
:status_path_configured_zh
echo 环境变量: ? 已配置
goto :status_file_info

:status_file_info
echo.
if "%LANG%"=="zh" goto :status_file_info_zh
echo JAR file info:
for %%F in ("%INSTALL_DIR%\%JAR_NAME%") do (
    echo   Size: %%~zF bytes
    echo   Modified: %%~tF
)
goto :status_end
:status_file_info_zh
echo JAR 文件信息:
for %%F in ("%INSTALL_DIR%\%JAR_NAME%") do (
    echo   大小: %%~zF 字节
    echo   修改时间: %%~tF
)
:status_end
echo.
pause
goto :main_menu

:status_not_installed
if "%LANG%"=="zh" goto :status_not_installed_zh
echo Status: ? Not installed
echo.
echo Use "Install" option to install BS Interpreter.
echo.
pause
goto :main_menu
:status_not_installed_zh
echo 状态: ? 未安装
echo.
echo 使用"安装"选项来安装 BS 解释器。
echo.
pause
goto :main_menu

:: ============================================
:: Test
:: ============================================
:test
cls
if "%LANG%"=="zh" goto :test_header_zh
goto :test_header_en

:test_header_zh
echo.
echo [测试 BS 解释器]
echo ========================================
echo.
goto :test_check

:test_header_en
echo.
echo [Test BS Interpreter]
echo ========================================
echo.
goto :test_check

:test_check
if not exist "%INSTALL_DIR%\%JAR_NAME%" goto :test_not_installed

if "%LANG%"=="zh" goto :test_1_zh
echo Test 1: Display help
echo ----------------------------------------
goto :test_1_run
:test_1_zh
echo 测试 1: 显示帮助信息
echo ----------------------------------------
:test_1_run

java -jar "%INSTALL_DIR%\%JAR_NAME%" --help
echo.
echo.

if "%LANG%"=="zh" goto :test_2_zh
echo Test 2: Execute infinite loop (auto-stop)
echo ----------------------------------------
goto :test_2_run
:test_2_zh
echo 测试 2: 执行无限循环程序（自动停止）
echo ----------------------------------------
:test_2_run

java -jar "%INSTALL_DIR%\%JAR_NAME%" -e 000000000000000 --lang %LANG%
echo.
echo.

if "%LANG%"=="zh" goto :test_success_zh
goto :test_success_en

:test_not_installed
if "%LANG%"=="zh" goto :test_not_installed_zh
echo BS Interpreter is not installed.
pause
goto :main_menu
:test_not_installed_zh
echo BS 解释器尚未安装。
pause
goto :main_menu

:test_success_zh
echo ? 测试完成！
echo.
echo 如果上述测试正常运行，说明 BS 解释器已正确安装。
echo 您可以在任何位置使用 bs 命令。
echo.
pause
goto :main_menu

:test_success_en
echo ? Test completed!
echo.
echo If the tests ran successfully, BS Interpreter is correctly installed.
echo You can use bs command from anywhere.
echo.
pause
goto :main_menu

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
