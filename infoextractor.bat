@echo off
setlocal EnableDelayedExpansion

rem ▶ Naviga nella cartella dove risiede questo script
pushd "%~dp0"

rem ▶ Definisci ROOT con backslash finale
set "ROOT=%CD%\"

rem ▶ Rimuovi il file di output se esiste
if exist allifos.txt del /F /Q allifos.txt

rem ▶ Funzione inline per processare un file
:processFile
  set "FILE=%~1"
  rem rimuovi ROOT dalla path assoluta per ottenere RELATIVE
  set "REL=!FILE:%ROOT%=!"
  >>allifos.txt echo ### File: !REL!
  >>allifos.txt echo ----------------------------------------
  >>allifos.txt type "!FILE!"
  >>allifos.txt echo.
  goto :eof

rem ▶ 1) Scansiona ricorsivamente tutti i pom.xml
for /R "%ROOT%" %%F in (pom.xml) do (
  call :processFile "%%F"
)

rem ▶ 2) Scansiona ricorsivamente tutti i file QUALSIASI sotto cartelle \src\
for /R "%ROOT%" %%F in (*) do (
  echo %%F | findstr /I "\\src\\" >nul
  if not errorlevel 1 (
    call :proce
