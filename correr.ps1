# Detiene cualquier Tomcat que siga usando el puerto 8080 y arranca el proyecto
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue |
    ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
Start-Sleep -Seconds 3
mvn clean package cargo:run
