$ErrorActionPreference = 'Stop'

$packageName   = 'kite-c4'
$toolsDir      = Join-Path (Get-ToolsLocation) $packageName
$url           = 'https://github.com/tristan852/kite/releases/download/v1.16.3/kite-1.16.3-windows-x64.zip'
$checksum      = 'BDE9EFF4C3705DEF63C2893373728896735993ED00850885FA02E719863A0585'
$checksumType  = 'sha256'

# Ensure tools directory exists
if (!(Test-Path $toolsDir)) {
    New-Item -ItemType Directory -Path $toolsDir | Out-Null
}

# Download and extract the ZIP package
Install-ChocolateyZipPackage `
    -PackageName $packageName `
    -Url $url `
    -Checksum $checksum `
    -ChecksumType $checksumType `
    -Destination $toolsDir `
    -RemoveZip

# Add tools folder to PATH
Install-ChocolateyPath $toolsDir
