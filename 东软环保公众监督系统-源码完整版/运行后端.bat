@echo off
chcp 65001 >nul
cd /d "%~dp0backend"
where mvn.cmd >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Maven，请确认 Maven 已安装并加入 PATH。
  pause
  exit /b 1
)
echo 正在启动 Spring Boot 后端，请不要关闭此窗口...
call mvn.cmd spring-boot:run
pause
