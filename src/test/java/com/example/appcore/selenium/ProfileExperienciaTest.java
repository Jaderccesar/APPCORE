package com.example.appcore.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileExperienciaTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:/Users/Pichau/AppData/Local/Google/Chrome/User Data");
        options.addArguments("profile-directory=Profile 2");
        options.addArguments("--disable-extensions");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void CT01_adicionarExperienciaComSucesso() {

        driver.get("https://www.google.com/");
        System.out.println("URL atual: " + driver.getCurrentUrl());
        driver.get("https://www.linkedin.com/feed/edit/forms/position/new/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        WebElement titulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("positionTitle")));
        titulo.sendKeys("Analista de Testes Júnior");

        WebElement empresa = driver.findElement(By.name("companyName"));
        empresa.sendKeys("CVS Provider");

        WebElement mesInicio = driver.findElement(By.name("startMonth"));
        Select selectMes = new Select(mesInicio);
        selectMes.selectByVisibleText("January");

        WebElement anoInicio = driver.findElement(By.name("startYear"));
        anoInicio.sendKeys("2025");

        WebElement btnSalvar = driver.findElement(By.xpath("//button[contains(@data-test-save-btn,'profileEdit')]"));
        btnSalvar.click();

        boolean voltouParaPerfil = wait.until(ExpectedConditions.urlContains("/in/"));
        assertTrue(voltouParaPerfil, "A experiência deveria ter sido salva e o perfil reaberto.");
    }

    @Test
    public void CT02_tentarSalvarSemTitulo() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // 1. Acessar o perfil
        driver.get("https://www.linkedin.com/in/me/");

        // 2. Clicar em “Adicionar experiência”
        WebElement botaoAddExp = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(@aria-label, 'Adicionar experiência')]")
                )
        );
        botaoAddExp.click();

        // 3. Deixar o título vazio (campo obrigatório)
        driver.findElement(By.id("company-typeahead")).sendKeys("CVS Provider");

        WebElement dataInicioMes = driver.findElement(By.id("date-range-form-component-profileEditFormElement-startDate-month"));
        dataInicioMes.sendKeys("01");

        WebElement dataInicioAno = driver.findElement(By.id("date-range-form-component-profileEditFormElement-startDate-year"));
        dataInicioAno.sendKeys("2025");

        // 4. Clicar em salvar
        WebElement botaoSalvar = driver.findElement(By.xpath("//button[contains(., 'Salvar')]"));
        botaoSalvar.click();

        // 5. Verificar mensagem de erro
        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p[contains(@class,'artdeco-inline-feedback') and contains(text(),'obrigatório')]")
                )
        );

        assertTrue(
                erro.getText().contains("obrigatório"),
                "Mensagem de erro não exibida ao tentar salvar sem título."
        );
    }
}