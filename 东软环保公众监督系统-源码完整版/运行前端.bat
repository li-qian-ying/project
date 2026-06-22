@echo off
chcp 65001 >nul
cd /d "%~dp0frontend"
if not exist node_modules call npm.cmd install
call npm.cmd run dev
pause
