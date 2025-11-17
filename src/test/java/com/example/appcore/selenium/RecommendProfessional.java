package com.example.appcore.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

public class RecommendProfessional extends BaseTest{

    private final String EMAIL = "leandrotestesoftware@gmail.com";     //colocar email válido
    private final String SENHA = "Leandro123@Teste";     //colocar senha válida

    @Test
    public void CT01_enviarRecomendacao() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        driver.get("https://www.linkedin.com/in/leandro-teste-1595b8396/");

        WebElement overflowBtn  = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"workspace\"]/div/div/div[1]/div/div/div[1]/div/section/div/div/div[2]/div[4]/div/div/button")
                )
        );
        overflowBtn.click();

        WebElement recomendarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[2]/div/div/div[3]/div")
                )
        );
        recomendarBtn.click();

        WebElement relacionamentoSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div/select")
                )
        );
        relacionamentoSelect.click();

        WebElement relacionamentoSelect2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div/select/option[13]")
                )
        );
        relacionamentoSelect2.click();

        WebElement cargoSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[2]/div[1]/div/select")
                )
        );
        cargoSelect.click();

        WebElement cargoSelect2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[2]/div[1]/div/select/option[2]")
                )
        );
        cargoSelect2.click();

        WebElement recomendacaoBox = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[3]/div[1]/textarea")
                )
        );

        String texto = "Leandro é um profissional altamente colaborativo em projetos de equipe.";
        recomendacaoBox.sendKeys(texto);

        WebElement enviarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"root\"]/dialog/div/div/div/div/div[2]/div/button")
                )
        );
        enviarBtn.click();

        WebElement pesquisarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/div[2]/div[2]/div[1]/header/div/div/div/div[2]/div/div/input")
                )
        );
        pesquisarBtn.click();

        driver.get("https://www.linkedin.com/in/leandro-silva-45911a392/");

        WebElement recomendacaoFornecidaBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/div[2]/div[2]/div[2]/div/main/div/div/div[1]/div/div/div[6]/div/div/div/section/div/div/div[2]/button[2]")
                )
        );
        recomendacaoFornecidaBtn.click();

        WebElement recomendacoesLista = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id=\"workspace\"]/div/div/div[1]/div/div/div[6]/div/div/div/section/div/div/div[3]/div/div")
                )
        );

        assertTrue(
                recomendacoesLista.getText().toLowerCase()
                        .contains("colaborativo"),
                "A recomendação não apareceu no perfil!"
        );
    }

    @Test
    public void CT02_solicitarRecomendacao() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        driver.get("https://www.linkedin.com/in/leandro-teste-1595b8396/");

        WebElement overflowBtn  = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"workspace\"]/div/div/div[1]/div/div/div[1]/div/section/div/div/div[2]/div[4]/div/div/button")
                )
        );
        overflowBtn.click();

        WebElement recomendarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[2]/div/div/div[2]")
                )
        );
        recomendarBtn.click();

        WebElement relacionamentoSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div/select")
                )
        );
        relacionamentoSelect.click();

        WebElement relacionamentoSelect2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[1]/div[1]/div/select/option[4]")
                )
        );
        relacionamentoSelect2.click();

        WebElement cargoSelect = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[2]/div[1]/div/select")
                )
        );
        cargoSelect.click();

        WebElement cargoSelect2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/dialog/div/div/div/div/div[1]/div[2]/div[2]/div[1]/div/select/option[2]")
                )
        );
        cargoSelect2.click();

        WebElement recomendacaoBox = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\":rh:\"]")
                )
        );

        String texto = "Gostaria de contar com sua recomendação sobre nosso trabalho juntos.";
        recomendacaoBox.sendKeys(texto);

        WebElement enviarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"root\"]/dialog/div/div/div/div/div[2]/div/button")
                )
        );
        enviarBtn.click();
    }
}
