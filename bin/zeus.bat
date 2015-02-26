@echo off

cd .. 
java -classpath lib\zeus.jar;lib\gnu-regexp.jar;lib\icu4j.jar;lib\jaxb-rt-1.0-ea.jar;lib\jaxb-xjc-1.0-ea.jar;lib\jena.jar;%classpath% zeus.generator.AgentGenerator

cd bin 
pause
