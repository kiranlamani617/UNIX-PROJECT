# PowerShell script to compile and start the Restaurant Management terminal app

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  Compiling & Starting Restaurant Management System Terminal App" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

if (-not (Test-Path bin)) {
    New-Item -ItemType Directory -Path bin | Out-Null
}

javac -d bin src/ds/*.java src/model/*.java src/service/*.java src/Main.java src/TestRunner.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nCompilation Successful! Launching application...`n" -ForegroundColor Green
    java -cp bin Main
} else {
    Write-Host "`nCompilation Failed!" -ForegroundColor Red
}
