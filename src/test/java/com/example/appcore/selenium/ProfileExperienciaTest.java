package com.example.appcore.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileExperienciaTest extends BaseTest {

    @Test
    public void CT01_adicionarExperienciaComSucesso() {

        driver.get("https://www.linkedin.com/checkpoint/lg/sign-in-another-account");

        driver.findElement(By.id("username")).sendKeys(""); 
        driver.findElement(By.id("password")).sendKeys("");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Abre o formulário
        driver.get("https://www.linkedin.com/feed/edit/forms/position/new/");

        // ===== TÍTULO =====
        WebElement titulo = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-title']")
        )); 
        titulo.sendKeys("Analista de Testes Júnior");

        // ===== EMPRESA =====
        WebElement empresa = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-requiredCompany']")
        ));
        empresa.sendKeys("CVS Provider");

        // ===== MÊS DE INÍCIO =====
        WebElement mesInicio = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("fieldset[data-test-date-dropdown='start'] select[data-test-month-select]")
        ));
        new Select(mesInicio).selectByVisibleText("Janeiro");

        // ===== ANO DE INÍCIO =====
        WebElement anoInicio = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("fieldset[data-test-date-dropdown='start'] select[data-test-year-select]")
        ));
        new Select(anoInicio).selectByVisibleText("2025");

        // Scroll
        js.executeScript("arguments[0].scrollIntoView(true);", anoInicio);

        // ===== LOCALIDADE =====
        // campo correto é geoPositionLocation
        WebElement local = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-geoPositionLocation']")
        ));
        local.sendKeys("Rio de Janeiro");

        // ===== TIPO DE LOCALIDADE (É UM SELECT!!!) =====
        WebElement tipoLocal = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id$='-locationType']")
        ));
        new Select(tipoLocal).selectByVisibleText("Remota");

        // ===== DESCRIÇÃO =====
        WebElement descricao = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("textarea[id$='-description']")
        ));
        descricao.sendKeys("Descrição automatizada do cargo.");

        // ===== TÍTULO DO PERFIL =====
        WebElement tituloPerfil = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-headline']")
        ));
        tituloPerfil.sendKeys("Analista de Testes");

        // ===== ONDE ENCONTROU O EMPREGO (TAMBÉM É SELECT) =====
        WebElement found = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id$='-sourceOfHire']")
        ));
        new Select(found).selectByVisibleText("LinkedIn");

        // Scroll para salvar
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        // ===== BOTÃO SALVAR =====
        WebElement salvar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Salvar')]")
        ));
        salvar.click();

        // ===== VALIDA SUCESSO
        WebElement celebracao = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("img.feed-shared-celebration-image__image")
                )
        );

        assertTrue(celebracao.isDisplayed());
        
    }

    @Test
    public void CT02_tentarSalvarSemTitulo() {

        driver.get("https://www.linkedin.com/checkpoint/lg/sign-in-another-account");

        driver.findElement(By.id("username")).sendKeys(""); 
        driver.findElement(By.id("password")).sendKeys("");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Abre o formulário
        driver.get("https://www.linkedin.com/feed/edit/forms/position/new/");

        // ===== TÍTULO =====
        //       Vazio

        // ===== EMPRESA =====
        WebElement empresa = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-requiredCompany']")
        ));
        empresa.sendKeys("CVS Provider");

        // ===== MÊS DE INÍCIO =====
        WebElement mesInicio = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("fieldset[data-test-date-dropdown='start'] select[data-test-month-select]")
        ));
        new Select(mesInicio).selectByVisibleText("Janeiro");

        // ===== ANO DE INÍCIO =====
        WebElement anoInicio = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("fieldset[data-test-date-dropdown='start'] select[data-test-year-select]")
        ));
        new Select(anoInicio).selectByVisibleText("2025");

        // Scroll
        js.executeScript("arguments[0].scrollIntoView(true);", anoInicio);

        // ===== LOCALIDADE =====
        // campo correto é geoPositionLocation
        WebElement local = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-geoPositionLocation']")
        ));
        local.sendKeys("Rio de Janeiro");

        // ===== TIPO DE LOCALIDADE (É UM SELECT!!!) =====
        WebElement tipoLocal = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id$='-locationType']")
        ));
        new Select(tipoLocal).selectByVisibleText("Remota");

        // ===== DESCRIÇÃO =====
        WebElement descricao = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("textarea[id$='-description']")
        ));
        descricao.sendKeys("Descrição automatizada do cargo.");

        // ===== TÍTULO DO PERFIL =====
        WebElement tituloPerfil = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[id$='-headline']")
        ));
        tituloPerfil.sendKeys("Analista de Testes");

        // ===== ONDE ENCONTROU O EMPREGO (TAMBÉM É SELECT) =====
        WebElement found = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[id$='-sourceOfHire']")
        ));
        new Select(found).selectByVisibleText("LinkedIn");

        // Scroll para salvar
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        // ===== BOTÃO SALVAR =====
        WebElement salvar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Salvar')]")
        ));
        salvar.click();

        // ===== VALIDA ERRO DE TÍTULO =====
        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".artdeco-inline-feedback__message")
                )
        );

        assertTrue(erro.isDisplayed());
        assertEquals("Título é um campo obrigatório", erro.getText().trim());

        
    }
}