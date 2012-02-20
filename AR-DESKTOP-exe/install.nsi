; install.nsi
;
; This script is based on example2.nsi,  remember the directory, 
; has uninstall support and (optionally) installs start menu shortcuts.
;
; It will install example2.nsi into a directory that the user selects,

;--------------------------------

; The name of the installer
Name "AR Desktop Installer"

; The file to write
OutFile "install.exe"

; The default installation directory
InstallDir $PROGRAMFILES\ARDesktop

; Registry key to check for directory (so if you install again, it will 
; overwrite the old one automatically)
InstallDirRegKey HKLM "Software\ARDESKTOP" "Install_Dir"

; Request application privileges for Windows Vista
RequestExecutionLevel admin

;--------------------------------

; Pages

Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

;--------------------------------

; The stuff to install
Section "ARDesktop (required)"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File "ar.exe"
  SetOutPath $INSTDIR\conf
  File /r .\conf\*.*
  SetOutPath $INSTDIR\models
  File /r .\models\*.*
  SetOutPath $INSTDIR\Data
  File /r .\Data\*.*
  SetOutPath $INSTDIR\libs
  file /r .\libs\*.*
  SetOutPath $INSTDIR\jre
  file /r .\jre\*.*

 ; CreateDirectory $INSTDIR\conf
   
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\ARDESKTOP "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ARDESKTOP" "ARDesktop" "ARDesktop"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ARDESKTOP" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ARDESKTOP" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ARDESKTOP" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\ARDesktop"
  CreateShortCut "$SMPROGRAMS\ARDesktop\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\ARDesktop\ARDesktop (MakeNSISW).lnk" "$INSTDIR\install.nsi" "" "$INSTDIR\install.nsi" 0
  
SectionEnd

;--------------------------------

; Uninstaller

Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ARDESKTOP"
  DeleteRegKey HKLM SOFTWARE\ARDESKTOP

  ; Remove files and uninstaller
  ;Delete $INSTDIR\install.nsi
  ;Delete $INSTDIR\uninstall.exe
   Delete $INSTDIR\*.*

  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\ARDesktop\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\ARDesktop"
  RMDir /r "$INSTDIR"

SectionEnd
