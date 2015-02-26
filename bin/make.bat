@echo off

cd ..

java -classpath %CLASSPATH%;lib\ant.jar org.apache.tools.ant.Main -f etc\build.xml %1

cd bin

pause