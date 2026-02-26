package com.example.automation.tests;

import com.microsoft.playwright.*;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Base class for all test classes.
 * Owns the full Playwright → Browser → BrowserContext → Page lifecycle.
 * Screenshots are captured automatically on test failure.
 *
 * Every test class should extend this and call super if overriding setUp/tearDown.
 */
public class BaseTest {

    protected Playwright    playwright;
    protected Browser       browser;
    protected BrowserContext context;
    protected Page          page;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        playwright = Playwright.create();

        // Launch real Chrome (not Chromium) in maximized, non-incognito mode
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setChannel("chrome")
                        .setArgs(Arrays.asList(
                                "--start-maximized",
                                "--disable-gpu",
                                "--no-sandbox",
                                "--disable-dev-shm-usage"
                        )));

        // Create a fresh context and page for each test — no shared state between tests
        context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(null));

        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Capture a full-page screenshot on failure before closing everything
        if (result.getStatus() == ITestResult.FAILURE && page != null) {
            try {
                java.nio.file.Path dir = Paths.get("target/screenshots");
                if (!Files.exists(dir)) Files.createDirectories(dir);
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                java.nio.file.Path dest = dir.resolve(timestamp + "_" + result.getName() + ".png");
                page.screenshot(new Page.ScreenshotOptions().setPath(dest).setFullPage(true));
                System.out.println("[Screenshot saved] " + dest.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("[Screenshot failed] " + e.getMessage());
            }
        }

        // playwright.close() disposes browser + context + pages in the right order.
        // Never call browser.close() separately — it causes a double-close crash.
        try { if (playwright != null) playwright.close(); } catch (Exception ignored) {}
        playwright = null;
        browser    = null;
        context    = null;
        page       = null;
    }
}

