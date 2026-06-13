package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for /ipal/aduan — the aduan list page.
 * Supports filtering, searching, and navigating to detail pages.
 */
public class AduanListPage extends BasePage {

    // --- Locators -----------------------------------------------------------

    // Status filter: <select> containing options for masuk/proses/selesai/etc.
    private static final By STATUS_FILTER = By.xpath(
            "//select[contains(@name,'status') or contains(@id,'status') "
            + "or contains(@name,'filter') or contains(@id,'filter')]");

    // Search input
    private static final By SEARCH_INPUT = By.xpath(
            "//input[@type='text' or @type='search']"
            + "[contains(@name,'search') or contains(@name,'keyword') "
            + "or contains(@name,'q') or contains(@id,'search') "
            + "or contains(@placeholder,'ari') or contains(@placeholder,'cari')]");

    // Table rows (any row in a <tbody>)
    private static final By TABLE_ROWS = By.cssSelector("table tbody tr");

    // Detail links: anchors pointing to /ipal/aduan/{id}
    private static final By DETAIL_LINKS = By.xpath(
            "//a[contains(@href,'/ipal/aduan/') "
            + "and not(contains(@href,'/ipal/aduan/create'))]");

    // Empty / no-data indicator
    private static final By EMPTY_STATE = By.xpath(
            "//*[contains(normalize-space(.),'tidak ada') "
            + "or contains(normalize-space(.),'Tidak ada') "
            + "or contains(normalize-space(.),'tidak ditemukan') "
            + "or contains(normalize-space(.),'Tidak ditemukan') "
            + "or contains(normalize-space(.),'belum ada') "
            + "or contains(normalize-space(.),'No data') "
            + "or contains(normalize-space(.),'Data tidak') "
            + "or contains(normalize-space(.),'kosong')]");

    // Status badge/label in a row — used to find first row with a given status
    private static final String STATUS_ROW_XPATH_TEMPLATE =
            "//table//tr[td[contains(normalize-space(.),'%s')]]";

    public AduanListPage(WebDriver driver) {
        super(driver);
    }

    // --- Navigation ---------------------------------------------------------

    public AduanListPage open() {
        driver.get(ConfigReader.baseUrl() + "/ipal/aduan");
        return waitLoaded();
    }

    public AduanListPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("/ipal/aduan"));
        return this;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/ipal/aduan");
    }

    // --- Filter & Search ----------------------------------------------------

    /**
     * Selects filter by status. Tries UI select first; falls back to URL param.
     */
    public AduanListPage filterByStatus(String status) {
        try {
            WebElement sel = waitVisible(STATUS_FILTER);
            new Select(sel).selectByValue(status);
            // Wait for stale or URL change after form auto-submit
            try {
                wait.until(ExpectedConditions.stalenessOf(sel));
            } catch (Exception ignored) {
                // Some filters update via AJAX without page reload — that's fine
            }
        } catch (Exception e) {
            // Fallback: navigate with query param (correct param name is status_aduan)
            driver.get(ConfigReader.baseUrl() + "/ipal/aduan?status_aduan=" + status);
        }
        waitLoaded();
        return this;
    }

    /**
     * Types keyword in search input. Empty string reloads full list.
     */
    public AduanListPage searchKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            driver.get(ConfigReader.baseUrl() + "/ipal/aduan");
            waitLoaded();
            return this;
        }
        try {
            WebElement input = waitVisible(SEARCH_INPUT);
            input.clear();
            input.sendKeys(keyword);
            input.submit();
        } catch (Exception e) {
            driver.get(ConfigReader.baseUrl() + "/ipal/aduan?search=" + keyword);
        }
        waitLoaded();
        // Wait for table to render — URL updates before DOM content after form submit
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(TABLE_ROWS),
                    ExpectedConditions.presenceOfElementLocated(EMPTY_STATE)));
        } catch (Exception ignored) {}
        return this;
    }

    // --- Row / State checks -------------------------------------------------

    public boolean hasRows() {
        try {
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            return rows.stream().anyMatch(r -> {
                try {
                    List<WebElement> tds = r.findElements(By.tagName("td"));
                    return !tds.isEmpty() && tds.size() > 1;
                } catch (StaleElementReferenceException ignored) {
                    return false;
                }
            });
        } catch (StaleElementReferenceException ignored) {
            return !driver.findElements(TABLE_ROWS).isEmpty();
        }
    }

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(EMPTY_STATE) || !hasRows();
    }

    // --- Navigation to detail -----------------------------------------------

    public AduanDetailPage clickFirstDetail() {
        WebElement link = wait.until(
                ExpectedConditions.elementToBeClickable(DETAIL_LINKS));
        link.click();
        return new AduanDetailPage(driver);
    }

    /**
     * Filters by status then opens the first matching aduan's detail page.
     * Returns null if no aduan with that status is available (instead of throwing).
     */
    public AduanDetailPage clickFirstDetailWithStatus(String status) {
        filterByStatus(status);

        // Try to find a row whose cells contain the status text (case-insensitive)
        By statusRow = By.xpath(String.format(STATUS_ROW_XPATH_TEMPLATE, status));
        List<WebElement> rows = driver.findElements(statusRow);

        if (!rows.isEmpty()) {
            try {
                WebElement detailLink = rows.get(0).findElement(
                        By.xpath(".//a[contains(@href,'/ipal/aduan/')]"));
                detailLink.click();
                return new AduanDetailPage(driver);
            } catch (Exception ignored) {}
        }

        // Fallback: check for any detail links after filtering (handles uppercase status text)
        List<WebElement> links = driver.findElements(DETAIL_LINKS);
        if (!links.isEmpty()) {
            links.get(0).click();
            return new AduanDetailPage(driver);
        }

        // No aduan with this status — return null instead of throwing
        return null;
    }
}
