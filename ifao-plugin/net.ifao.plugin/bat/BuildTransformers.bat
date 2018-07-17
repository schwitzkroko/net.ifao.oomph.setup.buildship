@echo off

if "%1"=="" goto error
set cdOLD=%cd%

echo --- remove old class directory ---
if exist classes rd classes /q /s
echo --- create new classes direcotry
md classes

echo --- compile java classes --- 
echo.>lst.txt
for %%i in (..\src\dtdinfo\*.java) do echo %%i>>lst.txt
for %%i in (..\src\dtdinfo\batch\*.java) do echo %%i>>lst.txt
for %%i in (..\src\dtdinfo\gui\*.java) do echo %%i>>lst.txt
for %%i in (..\src\net\ifao\xml\*.java) do echo %%i>>lst.txt
for %%i in (..\src\net\ifao\util\*.java) do echo %%i>>lst.txt
for %%i in (..\src\ifaoplugin\Util.java) do echo %%i>>lst.txt
for %%i in (..\src\net\ifao\dialogs\swing\*.java) do echo %%i>>lst.txt
javac -d classes -s ..\src -implicit:class @lst.txt
if errorlevel 1 goto error 

cd ..
echo --- start dtdinfo.CompareTransformers ---
java -Xmx512m -classpath "%cdOLD%/classes" dtdinfo.CompareTransformers %1
if errorlevel 1 goto error 
 
goto finished
:error
echo --- error occurred ---
:finished
echo --- cleanup ---
cd %cdOLD%
if exist classes rd classes /q /s
del lst.txt
