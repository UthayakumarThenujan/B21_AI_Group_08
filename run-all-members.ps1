# run-all-members.ps1
# Runs tests for each member separately and generates individual Allure reports.

$members = @(
    @{ Tag = "@Thenujan";    Name = "Thenujan" },
    @{ Tag = "@Asarak";      Name = "Asarak" },
    @{ Tag = "@Pirapanchan"; Name = "Pirapanchan" },
    @{ Tag = "@Sharhaan";    Name = "Sharhaan" }
)

$baseDir   = Split-Path -Parent $MyInvocation.MyCommand.Path
$targetDir = Join-Path $baseDir "target"

foreach ($m in $members) {
    $tag    = $m.Tag
    $name   = $m.Name
    $allureResults = Join-Path $targetDir "allure-results-$name"
    $allureReport  = Join-Path $targetDir "allure-report-$name"

    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host " Running tests for: $name ($tag)"       -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan

    # Clean previous results for this member
    if (Test-Path $allureResults) { Remove-Item $allureResults -Recurse -Force }
    New-Item -ItemType Directory -Path $allureResults | Out-Null

    # Run Cucumber tests with tag filter; redirect allure results to member-specific dir
    & mvn test `
        "-Dcucumber.filter.tags=$tag" `
        "-Dallure.results.directory=$allureResults" `
        "-DtestFailureIgnore=true" `
        --batch-mode `
        2>&1 | Tee-Object -FilePath (Join-Path $targetDir "test-log-$name.txt")

    Write-Host ""
    Write-Host " Generating Allure report for $name..." -ForegroundColor Yellow

    # Generate Allure report for this member
    & mvn allure:report `
        "-Dallure.results.directory=$allureResults" `
        "-Dallure.report.directory=$allureReport" `
        --batch-mode 2>&1 | Out-Null

    Write-Host " Report saved: $allureReport\index.html" -ForegroundColor Green
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host " ALL MEMBER REPORTS GENERATED SUCCESSFULLY " -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Reports:"
foreach ($m in $members) {
    $reportPath = Join-Path $targetDir "allure-report-$($m.Name)\index.html"
    Write-Host "  $($m.Name): $reportPath" -ForegroundColor White
}
