package com.example.appcore.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

public class MessagesTest extends BaseTest {

    private final String EMAIL = "leandrodasilvak00@gmail.com";     //colocar email válido
    private final String SENHA = "leandrodasilvak1";     //colocar senha válida

    @Test
    public void CT01_enviarMensagemDireta() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        WebElement mensagensBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href*='messaging']")
                )
        );

        mensagensBtn.click();

        WebElement searchBox = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Pesquisar mensagens']"))
        );

        searchBox.sendKeys("Leandro Teste");
        Thread.sleep(2000);
        searchBox.sendKeys(Keys.ENTER);

        WebElement messageBox = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("div.msg-form__contenteditable")
                )
        );

        String mensagem = "Olá, Leandro! Gostaria de falar sobre oportunidades profissionais.";
        messageBox.sendKeys(mensagem);

        WebElement sendBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.msg-form__send-button")
                )
        );
        sendBtn.click();

        WebElement lastMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("(//li[contains(@class,'msg-s-message-list__event')]//p[contains(@class,'msg-s-event-listitem__body')])[last()]")
                )
        );

        assertTrue(
                lastMessage.getText().contains(mensagem),
                "A mensagem enviada não apareceu corretamente!"
        );
    }

    @Test
    public void CT02_verificarSeHaConversasNaInbox() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        WebElement mensagensBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("a[href*='messaging']")
                )
        );
        mensagensBtn.click();

        WebElement listaConversas = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("ul.msg-conversations-container__conversations-list")
                )
        );

        List<WebElement> conversas = listaConversas.findElements(
                By.cssSelector("li.msg-conversation-listitem")
        );

        System.out.println("Conversas encontradas: " + conversas.size());

        assertTrue(
                conversas.size() > 0,
                "Nenhuma conversa foi encontrada na aba de mensagens!"
        );
    }
}
