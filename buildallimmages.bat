@echo off
setlocal enabledelayedexpansion

REM Elenco dei servizi e relativo tag immagine
set SERVICES=auth-service api-gateway dispensation-service inventory-service notification-service prescription-service pharma-service anagrafica-service

for %%S in (%SERVICES%) do (
    echo ==============================================
    echo Building %%S…
    docker build ^
      -f "%%S\Dockerfile" ^
      -t "tassproject/%%S:latest" ^
      .
    if errorlevel 1 (
        echo Errore durante il build di %%S, esco…
        exit /b 1
    )
)

echo ==============================================
echo ✅ Tutte le immagini Docker sono state buildate!
endlocal
