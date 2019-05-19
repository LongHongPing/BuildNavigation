@echo off & setlocal enabledelayedexpansion

set LIB_JARS=.\system\lib\*
set CONF_DIR=.\system\conf

if ""%1"" == ""debug"" goto debug
if ""%1"" == ""jmx"" goto jmx

java -Xms1024m -Xmx1024m -XX:MaxPermSize=256M -classpath %CONF_DIR%;%LIB_JARS% com.Application
goto end

:debug
java -Xms1024m -Xmx1024m -XX:MaxPermSize=256M -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n -classpath %CONF_DIR%;%LIB_JARS% com.Application
goto end

:jmx
java -Xms1024m -Xmx1024m -XX:MaxPermSize=256M -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -classpath %CONF_DIR%;%LIB_JARS% com.Application

:end
pause