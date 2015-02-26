Name "Zeus"
OutFile "ZeusInstaller.exe"

LicenseText "You must agree to this license before installing."
LicenseData "licenses\license.txt"

InstallDir "$PROGRAMFILES\Zeus"
InstallDirRegKey HKEY_LOCAL_MACHINE "SOFTWARE\BT Exact Technologies\Zeus" ""

DirShow show
DirText "Select the directory to install Zeus in:"

Section ""
SetOutPath "$INSTDIR"
File "README NOW.TXT"
File .zeus.prp
SectionEnd

Section ""
File /r doc
File /r src
SectionEnd

Section "" ;Registry
WriteRegStr HKEY_LOCAL_MACHINE "SOFTWARE\BT Exact Technologies\Zeus" "" "$INSTDIR"
WriteRegStr HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\Zeus" "DisplayName" "Zeus (remove only)"
WriteRegStr HKEY_LOCAL_MACHINE "Software\Microsoft\Windows\CurrentVersion\Uninstall\Zeus" "UninstallString" '"$INSTDIR\uninst.exe"'
WriteRegStr HKEY_USERS ".DEFAULT\Environment" "ZEUS_HOME" '"$INSTDIR"'
WriteUninstaller "$INSTDIR\uninst.exe"
SectionEnd

Section "" ; Libraries
SetOutPath "$INSTDIR\lib"
File lib\zeus.jar
File lib\jade.jar
File lib\gnu-regexp.jar
SectionEnd

Section "" ; Run files
SetOutPath "$INSTDIR\bin"
File bin\zeus.bat
File bin\make.bat
SectionEnd

Section "" ; Config
SetOutPath "$INSTDIR\etc"
File etc\build.xml
SectionEnd

Section "" ; Licenses
SetOutPath "$INSTDIR\licenses"
File licenses\license.html
File licenses\thirdpartydisclaimer.txt
File licenses\original_zeus_files.txt
SectionEnd

Section "" ; Gifs
SetOutPath "$INSTDIR\var\gifs\agentviewer"
File var\gifs\agentviewer\*.gif
SetOutPath "$INSTDIR\var\gifs\control"
File var\gifs\control\*.gif
SetOutPath "$INSTDIR\var\gifs\generator"
File var\gifs\generator\*.gif
SetOutPath "$INSTDIR\var\gifs\help"
File var\gifs\help\*.gif
SetOutPath "$INSTDIR\var\gifs\ontology"
File var\gifs\ontology\*.gif
SetOutPath "$INSTDIR\var\gifs\visualiser"
File var\gifs\visualiser\*.gif
SectionEnd

Section "" ; Help
SetOutPath "$INSTDIR\var"
File /r var\help
SectionEnd


UninstallText "This will uninstall Zeus from your system"
Section Uninstall
RMDir /r "$INSTDIR"
DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\BT Exact Technologies\Zeus"
DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Zeus"
DeleteRegValue HKEY_USERS ".DEFAULT\Environment" "ZEUS_HOME"
SectionEnd

