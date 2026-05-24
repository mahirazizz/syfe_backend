<#
Helper script: install Maven (if missing) and produce resolved dependency list for CVE scanning.
Run from project root in PowerShell (may require Administrator for package install).
#>

param(
    [string]$SessionDir = ".github\java-upgrade\20260524051948"
)

Set-StrictMode -Version Latest

function Ensure-SessionDir {
    if (-not (Test-Path $SessionDir)) {
        New-Item -ItemType Directory -Force -Path $SessionDir | Out-Null
    }
}

function Ensure-Maven {
    try {
        $mvn = (Get-Command mvn -ErrorAction Stop).Source
        Write-Host "Found mvn at: $mvn"
        return $true
    } catch {
        Write-Host "Maven not found on PATH. Attempting to install with winget..."
        try {
            winget install -e --id Apache.Maven -h
            return $true
        } catch {
            Write-Host "winget install failed or not available. Try installing Maven manually or via Chocolatey (choco install maven -y)."
            return $false
        }
    }
}

Ensure-SessionDir

if (-not (Ensure-Maven)) {
    Write-Host "Maven not available. After installing Maven, re-run this script to produce deps.txt."
    exit 1
}

Write-Host "Generating dependency list into $SessionDir\deps.txt"

try {
    & mvn dependency:list -DoutputAbsoluteArtifactId=true 2>&1 | Select-String "\[INFO\].*:.*:.*:.*:" | Out-File "$SessionDir\deps.txt"
    Write-Host "Dependency list saved to $SessionDir\deps.txt"
} catch {
    Write-Host "Failed to run mvn dependency:list. Ensure Maven runs successfully in this environment."
    exit 1
}

Write-Host "You can now run the CVE validator using the generated deps.txt."
