################################################################################
# run-all-reports.ps1 – Run ALL member tests + Bug tests, generate ALL reports
# Usage:  powershell -ExecutionPolicy Bypass -File run-all-reports.ps1
################################################################################

$ROOT = Split-Path -Parent $MyInvocation.MyCommand.Path

# Member config: Name, Runner class, Allure results dir, Report dir
$runs = @(
    @{ Name="Thenujan";    Runner="CategoryTestRunner"; Results="allure-results-Thenujan";    Report="allure-report-Thenujan"    },
    @{ Name="Asarak";      Runner="AuthTestRunner";     Results="allure-results-Asarak";      Report="allure-report-Asarak"      },
    @{ Name="Pirapanchan"; Runner="PlantTestRunner";    Results="allure-results-Pirapanchan"; Report="allure-report-Pirapanchan" },
    @{ Name="Sharhaan";    Runner="SalesTestRunner";    Results="allure-results-Sharhaan";    Report="allure-report-Sharhaan"    },
    @{ Name="BugReport";   Runner="BugReportRunner";    Results="allure-results-bugs";        Report="allure-report-bugs"        }
)

Write-Host ""
Write-Host "##############################################################" -ForegroundColor Cyan
Write-Host "#   ITQA Group 08 – Full Test Suite + Bug Report             #" -ForegroundColor Cyan
Write-Host "##############################################################" -ForegroundColor Cyan
Write-Host ""

$summary = @()

foreach ($r in $runs) {
    $resultsPath = "$ROOT\target\$($r.Results)"
    $reportPath  = "$ROOT\target\$($r.Report)"

    Write-Host "──────────────────────────────────────────────────" -ForegroundColor DarkCyan
    Write-Host " Running: $($r.Name) [$($r.Runner)]" -ForegroundColor Cyan
    Write-Host "──────────────────────────────────────────────────" -ForegroundColor DarkCyan

    # Clean
    if (Test-Path $resultsPath) { Remove-Item $resultsPath -Recurse -Force }
    New-Item -ItemType Directory -Path $resultsPath | Out-Null

    # Run tests
    $output = & mvn test `
        "-Dtest=$($r.Runner)" `
        "-Dallure.results.dir=$resultsPath" `
        --batch-mode 2>&1

    $output | Tee-Object -FilePath "$ROOT\target\log-$($r.Name).txt" | Out-Null

    # Extract pass/fail counts
    $resultLine = $output | Select-String "Tests run:" | Select-Object -Last 1
    $passed = if ($resultLine -match "Tests run: (\d+)") { $Matches[1] } else { "?" }
    $failed = if ($resultLine -match "Failures: (\d+)") { $Matches[1] } else { "0" }
    $errors = if ($resultLine -match "Errors: (\d+)")   { $Matches[1] } else { "0" }

    Write-Host " Result: $resultLine" -ForegroundColor $(if ($failed -eq "0" -and $errors -eq "0") { "Green" } else { "Yellow" })

    # Generate Allure report
    Write-Host " Generating Allure report..." -ForegroundColor Gray
    & mvn allure:report `
        "-Dallure.results.dir=$resultsPath" `
        "-Dallure.report.dir=$reportPath" `
        --batch-mode 2>&1 | Out-Null

    $summary += [PSCustomObject]@{
        Member  = $r.Name
        Runner  = $r.Runner
        Tests   = $passed
        Failures= $failed
        Errors  = $errors
        Report  = "$reportPath\index.html"
    }

    Write-Host " Report: $reportPath\index.html" -ForegroundColor Green
    Write-Host ""
}

# ── Final Summary ──────────────────────────────────────────────────────
Write-Host ""
Write-Host "##############################################################" -ForegroundColor Green
Write-Host "#              SUMMARY OF ALL TEST RUNS                      #" -ForegroundColor Green
Write-Host "##############################################################" -ForegroundColor Green
Write-Host ""
$summary | Format-Table -AutoSize
Write-Host ""
Write-Host "Report locations:" -ForegroundColor Cyan
foreach ($s in $summary) {
    Write-Host "  $($s.Member.PadRight(15)) --> $($s.Report)" -ForegroundColor White
}
Write-Host ""
Write-Host "To open a report, run:" -ForegroundColor Yellow
Write-Host "  Start-Process 'target\allure-report-Thenujan\index.html'" -ForegroundColor White
Write-Host "  Start-Process 'target\allure-report-bugs\index.html'" -ForegroundColor White
