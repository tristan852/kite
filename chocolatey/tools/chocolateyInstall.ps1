$ErrorActionPreference = 'Stop'

$packageName = 'kite-c4'
$url = 'https://github.com/tristan852/kite/releases/download/v1.16.2/kite-1.16.2-windows-x64.zip'
$toolsDir = "$(Split-Path -parent $MyInvocation.MyCommand.Definition)"
$zipPath = Join-Path $toolsDir 'kite-1.16.2-windows-x64.zip'

# Download the zip
Invoke-WebRequest -Uri $url -OutFile $zipPath

# --- SHA256 check ---
$expectedHash = '902806079259FF41E98AF96C6EE813A455B11F8500B942E76E376C82F8052F11'
$actualHash = (Get-FileHash $zipPath -Algorithm SHA256).Hash
if($actualHash -ne $expectedHash) { 
    throw "SHA256 mismatch! Expected $expectedHash but got $actualHash"
}
# --- End SHA256 check ---

# Extract the executable
Expand-Archive $zipPath -DestinationPath $toolsDir -Force

# Cleanup
Remove-Item $zipPath
