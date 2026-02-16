$ErrorActionPreference = 'Stop'

$packageName = 'kite-c4'
$toolsDir    = Join-Path $env:ChocolateyToolsLocation $packageName

# Remove installation folder
if (Test-Path $toolsDir) {
    Remove-Item $toolsDir -Recurse -Force
}

# Remove tools folder from PATH
if (Get-Command Remove-ChocolateyPath -ErrorAction SilentlyContinue) {
    Remove-ChocolateyPath -Path $toolsDir
}
