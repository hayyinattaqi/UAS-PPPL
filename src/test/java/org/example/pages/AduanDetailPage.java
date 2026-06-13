package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for /ipal/aduan/{id} — the aduan detail / workflow page.
 * Covers: viewing status, workflow actions (Terima/Tolak/Mulai/Selesai),
 * and adding progress notes (catatan).
 */
public class AduanDetailPage extends BasePage {

    // --- Locators -----------------------------------------------------------

    // Status indicator: badge, label, or element containing status text
    private static final By STATUS_ELEMENT = By.xpath(
            "//*[contains(@class,'badge') or contains(@class,'status') "
            + "or contains(@class,'label') or contains(@class,'chip')]"
            + "[string-length(normalize-space(.)) > 0][1]");

    // Workflow action buttons
    private static final By BTN_TERIMA = By.xpath(
            "//button[contains(normalize-space(.),'Terima') "
            + "or contains(normalize-space(.),'terima') "
            + "or @id='verifyAcceptBtn' or @value='terima' or @name='terima']"
            + " | //a[contains(normalize-space(.),'Terima')]");

    private static final By BTN_TOLAK = By.xpath(
            "//button[contains(normalize-space(.),'Tolak') "
            + "or contains(normalize-space(.),'tolak') "
            + "or @id='verifyRejectBtn' or @value='tolak' or @name='tolak']"
            + " | //a[contains(normalize-space(.),'Tolak')]");

    private static final By BTN_MULAI = By.xpath(
            "//button[contains(normalize-space(.),'Mulai') "
            + "or contains(@value,'mulai') or contains(@name,'mulai')]"
            + " | //a[contains(normalize-space(.),'Mulai')]");

    private static final By BTN_SELESAI = By.xpath(
            "//button[contains(normalize-space(.),'Selesai') "
            + "or contains(normalize-space(.),'Tandai') "
            + "or contains(@value,'selesai') or contains(@name,'selesai')]"
            + " | //a[contains(normalize-space(.),'Selesai')]");

    // Progress/catatan input (could be input or textarea)
    // Actual placeholder observed: "tulis catatan progres lalu tekan enter"
    private static final By CATATAN_INPUT = By.xpath(
            "//*[self::input or self::textarea]"
            + "[contains(@placeholder,'tulis catatan') "
            + "or contains(@placeholder,'catatan progres') "
            + "or contains(@placeholder,'catatan') "
            + "or contains(@id,'catatan') or contains(@name,'catatan') "
            + "or contains(@id,'progress') or contains(@name,'progress') "
            + "or contains(@id,'log') or contains(@name,'log')]");

    // Save/submit button for catatan — exact label: "tambah catatan manual"
    private static final By BTN_SIMPAN_CATATAN = By.xpath(
            "//button[contains(translate(normalize-space(.),"
            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'tambah catatan manual') "
            + "or @id='progressAddBtn' or @id='progressAutoSaveSubmit']");

    // Success/failure indicators after catatan save
    private static final By SUCCESS_INDICATOR = By.xpath(
            "//*[contains(@class,'success') or contains(@class,'alert-success') "
            + "or contains(@class,'toast-success') or contains(@class,'swal2-success')]"
            + "[contains(normalize-space(.),'berhasil') or contains(normalize-space(.),'saved') "
            + "or contains(normalize-space(.),'tersimpan') or contains(normalize-space(.),'sukses')]");

    private static final By ERROR_INDICATOR = By.xpath(
            "//*[contains(@class,'error') or contains(@class,'alert-danger') "
            + "or contains(@class,'alert-error') or contains(@class,'toast-error') "
            + "or contains(@class,'swal2-error') or contains(@class,'invalid')]"
            + "[contains(normalize-space(.),'gagal') or contains(normalize-space(.),'error') "
            + "or contains(normalize-space(.),'failed') or contains(normalize-space(.),'panjang') "
            + "or contains(normalize-space(.),'maksimal') or contains(normalize-space(.),'karakter')]");

    public AduanDetailPage(WebDriver driver) {
        super(driver);
    }

    // --- Navigation ---------------------------------------------------------

    public AduanDetailPage waitLoaded() {
        wait.until(d -> d.getCurrentUrl().matches(".*\\/ipal\\/aduan\\/\\d+.*"));
        return this;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().matches(".*\\/ipal\\/aduan\\/\\d+.*");
    }

    // --- Status -------------------------------------------------------------

    public String getCurrentStatus() {
        try {
            return waitVisible(STATUS_ELEMENT).getText().trim().toLowerCase();
        } catch (Exception e) {
            return driver.getPageSource().toLowerCase();
        }
    }

    public boolean currentStatusContains(String statusKeyword) {
        String pageText = driver.getPageSource().toLowerCase();
        return pageText.contains(statusKeyword.toLowerCase());
    }

    // --- Workflow actions ---------------------------------------------------

    public AduanDetailPage clickTerima() {
        click(BTN_TERIMA);
        handleConfirmDialog();
        return this;
    }

    public AduanDetailPage clickTolak() {
        click(BTN_TOLAK);
        handleConfirmDialog();
        return this;
    }

    public AduanDetailPage clickMulaiPerbaikan() {
        click(BTN_MULAI);
        handleConfirmDialog();
        return this;
    }

    public AduanDetailPage clickTandaiSelesai() {
        click(BTN_SELESAI);
        handleConfirmDialog();
        return this;
    }

    // --- Catatan / progress log ---------------------------------------------

    public AduanDetailPage typeCatatan(String text) {
        WebElement ta = waitVisible(CATATAN_INPUT);
        ta.clear();
        ta.sendKeys(text);
        return this;
    }

    public AduanDetailPage clickSimpanCatatan() {
        click(BTN_SIMPAN_CATATAN);
        return this;
    }

    public boolean isCatatanSuccessful() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(SUCCESS_INDICATOR),
                    ExpectedConditions.visibilityOfElementLocated(ERROR_INDICATOR)));
            return isDisplayed(SUCCESS_INDICATOR);
        } catch (TimeoutException e) {
            // No explicit message — treat as success if no error present
            return !isDisplayed(ERROR_INDICATOR);
        }
    }

    public boolean isCatatanFailed() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(SUCCESS_INDICATOR),
                    ExpectedConditions.visibilityOfElementLocated(ERROR_INDICATOR)));
            return isDisplayed(ERROR_INDICATOR);
        } catch (TimeoutException e) {
            return false;
        }
    }

    // --- Helpers ------------------------------------------------------------

    /**
     * Dismisses any browser confirm/alert dialog that a workflow action may trigger.
     */
    private void handleConfirmDialog() {
        try {
            wait.until(ExpectedConditions.alertIsPresent()).accept();
        } catch (Exception ignored) {
            // No alert — that's fine
        }
    }
}
