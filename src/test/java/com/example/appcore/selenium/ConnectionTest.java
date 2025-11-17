package com.example.appcore.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


public class ConnectionTest extends BaseTest{

    private WebDriver driver;
    private WebDriverWait wait;

    private final String URL_LOGIN = "https://www.linkedin.com/login";
    private final String USER_A_EMAIL = "leandrodasilvak00@gmail.com";
    private final String USER_A_PASS = "leandrodasilvak1";
    private final String USER_B_EMAIL = "jadercesar0812@gmail.com";
    private final String USER_B_PASS = "jader_1208";

    private final String USER_B_PROFILE_URL = "https://www.linkedin.com/in/jader-cesar-vanderlinde-a85326399/";
    private final String USER_A_PARTIAL_NAME = "Jader César Vanderlinde";

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    private void login(String email, String password) {
        driver.get(URL_LOGIN);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/feed"));
        closePopups();
    }

    private void logout() {
        try {
            By profileBtn = By.xpath("//button[contains(@aria-label,'Conta') or contains(@aria-label,'Account') or contains(@aria-label,'Me')]");
            wait.withTimeout(Duration.ofSeconds(4)).until(ExpectedConditions.elementToBeClickable(profileBtn)).click();
        } catch (Exception e) {
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                        "const sel = Array.from(document.querySelectorAll('button, img')).find(e => " +
                                " (e.getAttribute && (e.getAttribute('aria-label')||'').toLowerCase().includes('conta')) || " +
                                " (e.alt && (e.alt.toLowerCase().includes('você') || e.alt.toLowerCase().includes('you') || e.alt.toLowerCase().includes('profile'))) || " +
                                " (e.className && e.className.toLowerCase().includes('me')) );" +
                                " if(sel) sel.click();"
                );
            } catch (Exception ignored) {}
        }

        try {
            By logoutBtn = By.xpath("//a[contains(.,'Sair') or contains(.,'Sign out') or contains(.,'Log out')] | //button[contains(.,'Sair') or contains(.,'Sign out') or contains(.,'Log out')]");
            wait.withTimeout(Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
        } catch (Exception ignored) {}

        closePopups();
        removeOverlays();
        driver.manage().deleteAllCookies();
        driver.get(URL_LOGIN);
        wait.withTimeout(Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }

    private void closePopups() {
        try {
            wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@aria-label='Fechar']"))).click();
        } catch (Exception ignored) {}
    }

    private void removeOverlays() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelectorAll('[role=\"dialog\"], .artdeco-modal-overlay, .artdeco-toast-item').forEach(e => e.remove());");
        } catch (Exception ignored) {}
    }

    private void clickConnectButton() {
        closePopups();
        removeOverlays();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 300)");

        By connectBtn = By.xpath("//button[contains(., 'Conectar') or contains(., 'Connect')]");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(connectBtn));
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
            btn.click();
        } catch (Exception ignored) {}

        try {
            By popupOptions = By.xpath("//button[contains(., 'Enviar sem nota') or contains(., 'Send without note') or contains(., 'Agora não') or contains(., 'Not now')]");
            WebElement noNote = wait.withTimeout(Duration.ofSeconds(5)).until(ExpectedConditions.elementToBeClickable(popupOptions));
            noNote.click();
        } catch (TimeoutException ignored) {}
    }

    private String getConnectionStatus() {
        By statusButton = By.xpath("//button[contains(@aria-label,'Convidar') or contains(@aria-label,'Pendente') " +
                "or contains(@aria-label,'Conectado') or contains(.,'Mensagem') or contains(.,'Conectar')]");

        for (int i = 0; i < 5; i++) {
            try {
                WebElement btn = wait.withTimeout(Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOfElementLocated(statusButton));
                String text = btn.getText().trim();
                if (!text.isEmpty()) return text;
            } catch (TimeoutException e) {
                driver.navigate().refresh();
                closePopups();
                removeOverlays();
            }
        }
        throw new TimeoutException("Não foi possível localizar o status de conexão.");
    }

    @Test
    public void testConnectionFlow() throws InterruptedException {

        System.out.println("CT01 — Enviando Solicitação de Conexão");
        login(USER_A_EMAIL, USER_A_PASS);
        driver.get(USER_B_PROFILE_URL);
        closePopups();
        clickConnectButton();
        String statusA = getConnectionStatus();
        assertEquals("Pendente", statusA, "CT01: Status esperado 'Pendente' após envio.");
        logout();

        System.out.println("CT02 — Aceitando Solicitação");
        login(USER_B_EMAIL, USER_B_PASS);
        driver.get("https://www.linkedin.com/mynetwork/invitation-manager/");
        wait.until(ExpectedConditions.urlContains("invitation-manager"));
        closePopups();
        removeOverlays();

        boolean clicked = false;
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (int i = 0; i < 5; i++) {
            try {
                By acceptButtonLocator = By.xpath("//button[contains(@aria-label, 'Aceitar convite de " + USER_A_PARTIAL_NAME + "')]");
                WebElement acceptBtn = wait.withTimeout(Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(acceptButtonLocator));
                js.executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", acceptBtn);
                clicked = true;
                break;
            } catch (TimeoutException e) {
                js.executeScript("window.scrollBy(0, 400);");
                Thread.sleep(1000);
            }
        }

        if (!clicked) throw new TimeoutException("Não foi possível clicar no botão Aceitar do convite de " + USER_A_PARTIAL_NAME);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//button[contains(@aria-label, 'Aceitar convite de " + USER_A_PARTIAL_NAME + "')]")
        ));

        logout();

        login(USER_A_EMAIL, USER_A_PASS);
        driver.get(USER_B_PROFILE_URL);
        String finalStatus = getConnectionStatus();
        System.out.println("Status final: " + finalStatus);
        assertTrue(finalStatus.contains("Enviar mensagem") || finalStatus.contains("Conectado"),
                "CT02: Depois do aceite deve aparecer 'Mensagem' ou 'Conectado'");

        System.out.println("TESTE COMPLETO: RF04 / RT01 OK!");
    }
}