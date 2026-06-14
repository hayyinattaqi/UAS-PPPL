package org.example.steps;

import io.cucumber.java.en.*;
import org.example.pages.LoginPage;
import org.example.utils.DriverFactory;
import org.junit.jupiter.api.Assertions;

public class LoginSteps {

    LoginPage loginPage;

    @Given("User membuka halaman login BPALPJK")
    public void userMembukaHalamanLoginBPALPJK() {
        loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.openLoginPage();
    }

    @When("User mengetik nomor WhatsApp {string}")
    public void userMengetikNomorWhatsApp(String number) {
        loginPage.typeWhatsappNumber(number);
    }

    @Then("Field nomor WhatsApp menampilkan {string}")
    public void fieldNomorWhatsAppMenampilkan(String expectedValue) {
        Assertions.assertEquals(expectedValue, loginPage.getWhatsappValue());
    }

    @When("User mengklik tombol Kirim Kode OTP")
    public void userMengklikTombolKirimKodeOTP() {
        loginPage.clickSendOtpButton();
    }

    @Then("Sistem menampilkan validasi nomor WhatsApp")
    public void sistemMenampilkanValidasiNomorWhatsApp() {
        Assertions.assertTrue(
                loginPage.isErrorDisplayed()
                        || loginPage.isStillOnLoginPage()
        );
    }

    @When("User memilih tab Login dengan Email")
    public void userMemilihTabLoginDenganEmail() {
        loginPage.selectEmailTab();
    }

    @Then("Form login email ditampilkan")
    public void formLoginEmailDitampilkan() {
        Assertions.assertTrue(loginPage.isEmailFormDisplayed());
    }

    @When("User mengisi email {string}")
    public void userMengisiEmail(String email) {
        loginPage.typeEmail(email);
    }

    @When("User mengisi password {string}")
    public void userMengisiPassword(String password) {
        loginPage.typePassword(password);
    }

    @When("User mengklik tombol Login")
    public void userMengklikTombolLogin() {
        loginPage.clickMasukButton();
    }

    @Then("User berhasil masuk ke dashboard BPALPJK")
    public void userBerhasilMasukKeDashboardBPALPJK() {
        Assertions.assertTrue(loginPage.isOnDashboardPage());
    }

    @Then("Sistem menampilkan validasi email dan password")
    public void sistemMenampilkanValidasiEmailDanPassword() {
        Assertions.assertTrue(
                loginPage.isErrorDisplayed()
                        || loginPage.isStillOnLoginPage()
        );
    }

    @Then("Sistem menampilkan pesan login gagal")
    public void sistemMenampilkanPesanLoginGagal() {
        Assertions.assertTrue(
                loginPage.isErrorDisplayed()
                        || loginPage.isStillOnLoginPage()
        );
    }

    @When("User mengklik link Lupa kata sandi")
    public void userMengklikLinkLupaKataSandi() {
        loginPage.clickForgotPassword();
    }

    @Then("User diarahkan ke halaman lupa kata sandi")
    public void userDiarahkanKeHalamanLupaKataSandi() {
        Assertions.assertTrue(loginPage.isForgotPasswordPageDisplayed());
    }
}