package com.example.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestNG listener that builds an ExtentReports HTML report under
 * target/extent-reports/ExtentReport_<timestamp>.html
 *
 * Screenshots saved by BaseTest are embedded automatically when a test fails.
 */
public class ExtentReportListener implements ITestListener {

    private static final String REPORT_DIR     = "target/extent-reports/";
    private static final String SCREENSHOT_DIR = "screenshots/";   // relative to target/

    private static ExtentReports extent;
    // Thread-local so parallel tests each get their own ExtentTest node
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    // -------------------------------------------------------------------------
    // Suite-level: create the report once when the suite starts
    // -------------------------------------------------------------------------
    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportPath = REPORT_DIR + "ExtentReport_" + timestamp + ".html";

            new File(REPORT_DIR).mkdirs();

            ExtentHtmlReporter html = new ExtentHtmlReporter(reportPath);
            html.config().setDocumentTitle("Playwright Automation Report");
            html.config().setReportName("Shady Meadows B&B – Test Execution Report");
            html.config().setTheme(Theme.DARK);
            html.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(html);
            extent.setSystemInfo("Application", "https://automationintesting.online/");
            extent.setSystemInfo("Browser", "Chrome (headed)");
            extent.setSystemInfo("Framework", "Playwright Java + TestNG");
            extent.setSystemInfo("Author", "QA Automation");

            System.out.println("[ExtentReport] Report will be saved to: "
                    + Paths.get(reportPath).toAbsolutePath());
        }
    }

    // -------------------------------------------------------------------------
    // Test-level hooks
    // -------------------------------------------------------------------------
    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        String testName    = (description == null || description.isEmpty())
                ? result.getName()
                : description;

        ExtentTest test = extent.createTest(testName);
        extentTest.set(test);
        test.assignCategory(result.getMethod().getGroups());
        test.info("Test started: <b>" + result.getName() + "</b>");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().pass("Test <b>PASSED</b>");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTest.get();
        test.fail("Test <b>FAILED</b>");
        test.fail(result.getThrowable());   // logs the full stack trace

        // Attach screenshot saved by BaseTest
        attachScreenshot(test, result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().skip("Test <b>SKIPPED</b>");
        if (result.getThrowable() != null) {
            extentTest.get().skip(result.getThrowable());
        }
    }

    // -------------------------------------------------------------------------
    // Suite-level: flush the report once when the suite finishes
    // -------------------------------------------------------------------------
    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            System.out.println("[ExtentReport] Report flushed successfully.");
        }
    }

    // -------------------------------------------------------------------------
    // Helper – find the most-recently saved screenshot and embed it
    // -------------------------------------------------------------------------
    private void attachScreenshot(ExtentTest test, String testName) {
        try {
            File screenshotDir = new File("target/" + SCREENSHOT_DIR);
            if (!screenshotDir.exists()) return;

            File[] matches = screenshotDir.listFiles(
                    (dir, name) -> name.endsWith("_" + testName + ".png"));

            if (matches == null || matches.length == 0) return;

            // Pick the most recent file if there are multiple
            File latest = matches[0];
            for (File f : matches) {
                if (f.lastModified() > latest.lastModified()) latest = f;
            }

            // Relative path so the HTML report works from any machine
            String relativePath = "../" + SCREENSHOT_DIR + latest.getName();
            test.addScreenCaptureFromPath(relativePath, "Failure Screenshot");
            test.info("Screenshot: <b>" + latest.getName() + "</b>");

        } catch (Exception e) {
            test.warning("[Could not attach screenshot] " + e.getMessage());
        }
    }
}
