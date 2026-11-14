$ErrorActionPreference = 'Stop'

Push-Location "$PSScriptRoot\..\backend"
try {
    ./mvnw.cmd verify
}
finally {
    Pop-Location
}

Push-Location "$PSScriptRoot\..\frontend"
try {
    npm ci
    npm test
    npm run lint
    npm run build
}
finally {
    Pop-Location
}

Write-Host 'Verificación completada correctamente.' -ForegroundColor Green

