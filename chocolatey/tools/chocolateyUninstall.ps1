$ErrorActionPreference = 'Stop'

$toolsDir = "$(Split-Path -Parent $MyInvocation.MyCommand.Definition)"

# Remove installation folder
if (Test-Path $toolsDir) {
    Remove-Item $toolsDir -Recurse -Force
}

# Remove tools folder from PATH
if (Get-Command Remove-ChocolateyPath -ErrorAction SilentlyContinue) {
    Remove-ChocolateyPath -Path $toolsDir
}
