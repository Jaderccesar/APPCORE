package com.example.appcore.selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.*;

public class LinkedInNotificationsTest extends BaseTest {

    WebDriverWait wait;

    String USER = "jadercesar0812@gmail.com";
    String PASS = "jader_1208";

    @BeforeEach
    public void setupWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    void login() {
        driver.get("https://www.linkedin.com/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys(USER);
        driver.findElement(By.id("password")).sendKeys(PASS);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("img.global-nav__me-photo")
        ));
    }

    void openNotifications() {

        By[] selectors = new By[]{
                By.cssSelector("a[data-test-global-nav-link='notifications']"),
                By.cssSelector("a[href*='notifications']"),
                By.cssSelector("button[aria-label*='Notifica']"),
                By.xpath("//button[contains(@aria-label,'Notifi')]"),
                By.xpath("//a[contains(@href,'/notifications')]"),
                By.xpath("//li-icon[contains(@type,'bell')]/ancestor::a"),
                By.xpath("//li-icon[contains(@type,'bell')]/ancestor::button"),
                By.xpath("//span[contains(text(),'Notificações')]/ancestor::a")
        };

        WebElement bell = null;

        for (By sel : selectors) {
            try {
                bell = wait.until(ExpectedConditions.presenceOfElementLocated(sel));
                if (bell != null) break;
            } catch (Exception ignored) {}
        }

        Assertions.assertNotNull(bell,
                "Não consegui localizar o botão de NOTIFICAÇÕES no LinkedIn. O DOM mudou.");

        try {
            wait.until(ExpectedConditions.elementToBeClickable(bell));
            bell.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bell);
        }

        By[] panels = new By[]{
                By.cssSelector("div.notifications-container"),
                By.cssSelector("div[role='main']"),
                By.cssSelector("div[data-test-notifications-list]"),
                By.xpath("//h1[contains(text(),'Notificações')]/ancestor::div")
        };

        for (By p : panels) {
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(p));
                return;
            } catch (Exception ignored) {}
        }

    }

    boolean pageContainsIgnoreCase(String text) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String bodyText = (String) js.executeScript("return document.body.innerText.toLowerCase();");
        return bodyText.contains(text.toLowerCase());
    }

    @Test
    @DisplayName("CT01 - Verifica se existe 'viu seu perfil' nas notificações")
    void testProfileViewAlert() {
        login();
        openNotifications();

        boolean found =
                pageContainsIgnoreCase("viu seu perfil") ||
                        pageContainsIgnoreCase("ver todas as visualizações") ||
                        pageContainsIgnoreCase("visualizações");

        Assertions.assertTrue(found,
                "Nenhuma notificação contendo algo como 'viu seu perfil' foi encontrada.");
    }

    void openMessages() {
        WebElement msgBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[@title='Mensagens']/ancestor::a")
        ));

        msgBtn.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/messaging"),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.msg-conversations-container"))
        ));
    }

    @Test
    @DisplayName("CT02 — Verifica existência de mensagem recebida")
    void testReceivedMessage() {

        login();
        openMessages();

        List<WebElement> messages = driver.findElements(
                By.xpath("//*[contains(@class,'msg')]//*[normalize-space(text())]")
        );

        boolean found = false;

        for (WebElement m : messages) {
            try {
                String txt = m.getText().trim().toLowerCase();
                if (!txt.isBlank()) {
                    found = true;
                    break;
                }
            } catch (Exception ignored) {}
        }

        Assertions.assertTrue(found,
                "Nenhuma mensagem encontrada na área de Mensagens.");
    }
}