package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By ADMIN_LOGIN_LINK =
            By.xpath("//*[self::a or self::button][contains(normalize-space(.),'Admin Login')]");

    private static final By EMAIL_TAB = By.id("email-tab");
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By MASUK_BUTTON =
            By.cssSelector("#email-login-form button[type='submit']");

    private static final By WHATSAPP_INPUT = By.id("nomor_hp");
    private static final By SEND_OTP_BUTTON =
            By.xpath("//input[@id='nomor_hp']/following::button[1]");

    private static final By FORGOT_PASSWORD_LINK =
            By.xpath("//a[contains(normalize-space(),'Lupa kata sandi')]");

    private static final By ERROR_MESSAGE =
            By.xpath("//*[contains(text(),'required') " +
                    "or contains(text(),'wajib') " +
                    "or contains(text(),'salah') " +
                    "or contains(text(),'gagal') " +
                    "or contains(text(),'credentials') " +
                    "or contains(text(),'These credentials')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage openMap() {
        driver.get(ConfigReader.mapUrl());
        return this;
    }

    public LoginPage openLoginPage() {
        driver.get("https://bpalpjk.madanateknologi.web.id/login");
        return this;
    }

    public LoginPage clickAdminLogin() {
        click(ADMIN_LOGIN_LINK);
        return this;
    }

    public LoginPage selectEmailTab() {
        click(EMAIL_TAB);
        return this;
    }

    public MainDashboardPage loginWithEmail(String email, String password) {
        type(EMAIL_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(MASUK_BUTTON);
        return new MainDashboardPage(driver);
    }

    public void typeWhatsappNumber(String number) {
        type(WHATSAPP_INPUT, number);
    }

    public String getWhatsappValue() {
        return driver.findElement(WHATSAPP_INPUT).getAttribute("value");
    }

    public void clickSendOtpButton() {
        click(SEND_OTP_BUTTON);
    }

    public boolean isEmailFormDisplayed() {
        return driver.findElements(EMAIL_INPUT).size() > 0
                && driver.findElements(PASSWORD_INPUT).size() > 0;
    }

    public void typeEmail(String email) {
        type(EMAIL_INPUT, email);
    }

    public void typePassword(String password) {
        type(PASSWORD_INPUT, password);
    }

    public void clickMasukButton() {
        click(MASUK_BUTTON);
    }

    public boolean isErrorDisplayed() {
        return driver.findElements(ERROR_MESSAGE).size() > 0;
    }

    public boolean isStillOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    public void clickForgotPassword() {
        click(FORGOT_PASSWORD_LINK);
    }

    public boolean isForgotPasswordPageDisplayed() {
        String currentUrl = driver.getCurrentUrl().toLowerCase();

        return currentUrl.contains("forgot")
                || currentUrl.contains("reset")
                || currentUrl.contains("password")
                || currentUrl.contains("lupa");
    }

    public boolean isOnDashboardPage() {
        String currentUrl = driver.getCurrentUrl();

        return currentUrl.equals("https://bpalpjk.madanateknologi.web.id/dashboard")
                || currentUrl.equals("https://bpalpjk.madanateknologi.web.id/dashboard/");
    }
}