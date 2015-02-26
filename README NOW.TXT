How to install and run Zeus. 
---------------------------

1. Read the license(s) for all components

2. There is no step 2. 

3. You must also copy .zeus.prp to your home directory. On most windows machines this is c:\documents and settings\YOUR_USER_NAME. If you are using UNIX you know exactly where it should go. You must edit the file to make the zeus install.directory property point to the place you have put zeus on your machine. 
The critical element of this file is the Install.dir entry. This is what the system uses to bootstrap itself on a machine to find the gifs and help files it needs. 
These files are in .\var\help and .\var\gifs; it is an exercise for the user to figure out how to make their particular machines environment cough up the values that Zeus needs (see the comment about UNIX above). 

4. You must update your classpath to include 

.\lib\zeus.jar
.\lib\gnu-regexp.jar
.\lib\icu4j.jar
.\lib\sl.jar 

and 

.\lib\jaxb-rt-1.0-ea.jar 
.\lib\jaxb-xjc-1.0-ea.jar  
.\lin\xerces.jar

if you want to build Zeus also add 

.\lib\ant.jar

