@echo off

if not exist "%JAVA_HOME%\bin\java.exe" echo Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better! & EXIT /B 1
set "JAVA=%JAVA_HOME%\bin\java.exe"

setlocal enabledelayedexpansion

set BASE_DIR=%~dp0
rem added double quotation marks to avoid the issue caused by the folder names containing spaces.
rem removed the last 5 chars(which means \bin\) to get the base DIR.
set BASE_DIR="%BASE_DIR:~0,-5%"

set CUSTOM_SEARCH_LOCATIONS=file:%BASE_DIR%/conf/

set SERVER=G-Cloud-server

set "PAN_JVM_OPTS=-Xms512m -Xmx512m -Xmn256m"

rem set pan server options
set "PAN_OPTS=%PAN_OPTS% -jar %BASE_DIR%\target\%SERVER%.jar"

rem set pan server spring com.grazy.config location
set "PAN_CONFIG_OPTS=--spring.com.grazy.config.additional-location=%CUSTOM_SEARCH_LOCATIONS%"

rem set pan server log4j file location
set "PAN_LOG4J_OPTS=--logging.com.grazy.config=%BASE_DIR%/conf/G-Cloud-server-logback.xml"


set COMMAND="%JAVA%" %PAN_JVM_OPTS% %PAN_OPTS% %PAN_CONFIG_OPTS% %PAN_LOG4J_OPTS%  %*

echo "G-Cloud server is starting..."
rem start pan server command
%COMMAND%
echo "G-Cloud server is started!"