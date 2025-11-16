package com.example.appcore.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;

public class GroupTest extends BaseTest{

    private final String EMAIL = "leandrodasilvak00@gmail.com";     //colocar email válido
    private final String SENHA = "leandrodasilvak1";     //colocar senha válida

    @Test
    public void CT01_participarGrupo() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        WebElement pesquisar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/div[2]/div[2]/div[1]/header/div/div/div/div[2]/div/div/input")
                )
        );
        pesquisar.click();

        String texto = "software";
        pesquisar.sendKeys(texto);
        pesquisar.sendKeys(Keys.ENTER);

        WebElement gruposBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[6]/div[3]/div[2]/section/div/nav/div/ul/li[6]")
                )
        );
        gruposBtn.click();

        WebElement participarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[6]/div[3]/div[2]/div/div[1]/main/div/div/div[2]/div/ul/li[1]/div/div/div/div[3]/div")
                )
        );
        participarBtn.click();

        driver.get("https://www.linkedin.com/in/leandro-silva-6a3484289/");

        WebElement grupoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/div[2]/div[2]/div[2]/div/main/div/div/div[1]/div/div/div[9]/div/div/section/div/div/div[2]/button[2]")
                )
        );
        grupoBtn.click();

        WebElement grupoLista = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/div/div[2]/div[2]/div[2]/div/main/div/div/div[1]/div/div/div[9]/div/div/section/div/div/div[3]/div/div")
                )
        );

        assertTrue(
                grupoLista.getText().toLowerCase()
                        .contains("software"),
                "O grupo nao apareceu no perfil!"
        );
    }

    @Test
    public void CT02_participarGrupoPrivado() throws InterruptedException {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys(EMAIL);
        driver.findElement(By.id("password")).sendKeys(SENHA);
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        wait.until(ExpectedConditions.urlContains("/feed"));

        WebElement pesquisar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div/div[2]/div[2]/div[1]/header/div/div/div/div[2]/div/div/input")
                )
        );
        pesquisar.click();

        String texto = "Grupo para materia de testes";
        pesquisar.sendKeys(texto);
        pesquisar.sendKeys(Keys.ENTER);

        WebElement grupoFiltradoBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[6]/div[3]/div[2]/section/div/nav/div/ul/li[3]")
                )
        );
        grupoFiltradoBtn.click();

        WebElement participarBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("/html/body/div[6]/div[3]/div[2]/div/div[1]/main/div/div/div[2]/div/ul/li[1]/div/div/div/div[3]/div")
                )
        );
        participarBtn.click();

        driver.get("https://www.linkedin.com/groups/");

        WebElement solicitadaBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"ember33\"]")
                )
        );
        solicitadaBtn.click();

        WebElement solicitacoesList = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"ember30\"]/div[2]/div")
                )
        );
        solicitacoesList.click();

        assertTrue(
                solicitacoesList.getText().toLowerCase()
                        .contains("testes"),
                "O grupo nao apareceu na listagem!"
        );

    }

}
