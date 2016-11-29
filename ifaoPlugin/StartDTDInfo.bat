@echo off

set START_JAVA=start javaw
if "%1"=="cmd" set START_JAVA=start java

REM --- Get the classpath ---
set cp=classes;bin

REM --- start the requester
%START_JAVA% -Xmx512m -classpath "%cp%" dtdinfo.DtdMain 