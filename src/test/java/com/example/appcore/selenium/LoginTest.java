package com.example.appcore.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

public class LoginTest extends BaseTest {

    @Test
    public void CT01_loginComSucesso() {

        driver.get("https://www.linkedin.com/checkpoint/lg/sign-in-another-account?trk=guest_homepage-basic_nav-header-signin");

        driver.findElement(By.id("username")).sendKeys(""); //colocar email válido
        driver.findElement(By.id("password")).sendKeys(""); //colocar senha válida

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

        // espera até que a URL contenha "/feed"
        boolean carregouFeed = wait.until(ExpectedConditions.urlContains("/feed"));

        assertTrue(carregouFeed, "O login não redirecionou para o feed!");
    }

    @Test
    public void CT02_loginInvalido() {

        driver.get("https://www.linkedin.com/login");

        driver.findElement(By.id("username")).sendKeys("invalido@gmail.com");
        driver.findElement(By.id("password")).sendKeys("");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement errorMsg = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("error-for-password"))
        );

        assertTrue(errorMsg.getText().contains("Insira uma senha"));
    }
}