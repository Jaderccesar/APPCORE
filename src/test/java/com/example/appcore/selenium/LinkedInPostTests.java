package com.example.appcore.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LinkedInPostTests extends BaseTest {

    private WebDriverWait wait;

    private final String USER = "jadercesar0812@gmail.com";
    private final String PASS = "jader_1208";

    @BeforeEach
    public void initWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    private void login() {
        driver.get("https://www.linkedin.com/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(USER);
        driver.findElement(By.id("password")).sendKeys(PASS);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img.global-nav__me-photo")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[aria-label='Me']"))
        ));
    }

    private void openStartPost() {
        By[] selectors = new By[]{
                By.cssSelector("button.share-box__open"),
                By.xpath("//button[contains(., 'Começar') or contains(., 'Start a post')]"),
                By.cssSelector("div.share-box-feed-entry__top-bar button")
        };

        WebElement btn = null;

        for (By sel : selectors) {
            try {
                btn = wait.until(ExpectedConditions.elementToBeClickable(sel));
                if (btn != null) break;
            } catch (Exception ignored) {}
        }

        Assertions.assertNotNull(btn, "Não encontrei o botão 'Começar publicação'.");

        try { btn.click(); }
        catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[role='textbox'], div[contenteditable='true']")));
    }

    private void handlePublicationSettingsPopup() {
        try {
            WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.artdeco-modal__content")));

            WebElement doneBtn = dialog.findElement(By.cssSelector("button.artdeco-button--primary"));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", doneBtn);

            wait.until(ExpectedConditions.invisibilityOf(dialog));

        } catch (Exception ignored) {
            System.out.println("Modal de configurações não apareceu.");
        }
    }

    private void publishPostSimple(String content) {

        openStartPost();
        handlePublicationSettingsPopup();

        WebElement editor = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div[role='textbox'], div[contenteditable='true']")));

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("arguments[0].innerText = '';", editor);

        js.executeScript("arguments[0].innerText = arguments[1];", editor, content);

        js.executeScript("""
            const evt = new Event('input', { bubbles: true });
            arguments[0].dispatchEvent(evt);
        """, editor);

        handlePublicationSettingsPopup();

        WebElement publishBtn = null;

        try {
            publishBtn = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("button.share-actions__primary-action:not(.artdeco-button--disabled)")
                    ));
        } catch (Exception ignored) {
            publishBtn = driver.findElement(By.cssSelector("button.share-actions__primary-action"));
        }

        Assertions.assertNotNull(publishBtn, "Botão 'Publicar' não encontrado.");

        try { publishBtn.click(); }
        catch (Exception ignored) {
            js.executeScript("arguments[0].click();", publishBtn);
        }

        try { Thread.sleep(3000); } catch (Exception ignored) {}
    }

    private boolean feedContainsText(String text) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String body = (String) js.executeScript("return document.body.innerText || '';");
        return body.toLowerCase().contains(text.toLowerCase());
    }

    @Test
    @DisplayName("CT01 - Publicar atualização de texto simples")
    void ct01_publishSimpleUpdate() {

        login();

        String content = "Hoje finalizei meu primeiro módulo de estudo! " + System.currentTimeMillis();

        publishPostSimple(content);

        boolean found = feedContainsText(content);
        Assertions.assertTrue(found, "Post simples não apareceu no feed.");
    }

    @Test
    @DisplayName("CT02 - Publicar post longo (>500 caracteres)")
    void ct02_publishLongPost() {

        login();

        String base = "ABCDEFGHILMNOPQRSTUVXZ ";
        StringBuilder sb = new StringBuilder();

        while (sb.length() < 500) {
            sb.append(base);
        }

        String longPost = "POST LONGO DE TESTE: " + sb.substring(0, 500);

        publishPostSimple(longPost);

        boolean found = feedContainsText("POST LONGO DE TESTE");
        Assertions.assertTrue(found, "Post longo não apareceu no feed.");
    }
}