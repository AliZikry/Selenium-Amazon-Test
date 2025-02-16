package amazonTestCases;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class buyVideoGame {
    private WebDriver driver;
    private WebDriverWait wait;
    private int countUnder15000 = 0;
    private int totalBelow15000 = 0;

    @BeforeMethod
    public void openWebsite() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void amazonTest() throws InterruptedException, AWTException {
        driver.navigate().to("https://www.amazon.eg/");
        changeLanguageToEnglish();
        login(System.getenv("AMAZON_EMAIL"), System.getenv("AMAZON_PASSWORD"));
        navigateToVideoGames();
        applyFiltersAndSort();
        addEligibleProductsToCart();
        validateCartAndCheckout();
        goToCheckoutAddAddress();
        validateCheckoutTotalPrice();
    }

    private void changeLanguageToEnglish() {
        driver.findElement(By.id("icp-nav-flyout")).click();
        driver.findElement(By.xpath("//*[@id=\"icp-language-settings\"]/div[3]/div/label/i")).click();
        driver.findElement(By.xpath("//*[@id=\"icp-save-button\"]/span/input")).click();
        wait.until(ExpectedConditions.urlContains("?language=en_AE"));
    }

    private void login(String email, String password) throws AWTException {
        driver.findElement(By.id("nav-link-accountList-nav-line-1")).click();
        WebElement emailField = driver.findElement(By.name("email"));
        emailField.sendKeys(email);
        wait.until(ExpectedConditions.textToBePresentInElementValue(emailField, email));
        driver.findElement(By.id("continue")).click();

        Robot robot = new Robot();
        robot.delay(2000);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);

        WebElement passwordField = driver.findElement(By.name("password"));
        passwordField.sendKeys(password);
        wait.until(ExpectedConditions.textToBePresentInElementValue(passwordField, password));
        driver.findElement(By.id("signInSubmit")).click();
    }

    private void navigateToVideoGames() throws InterruptedException {
        driver.findElement(By.id("nav-hamburger-menu")).click();
        Thread.sleep(2000);
        WebElement all = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".hmenu-item.hmenu-compressed-btn")));
        all.click();
        Thread.sleep(3000);
        driver.findElement(By.cssSelector("[data-menu-id=\"16\"]")).click();
        Thread.sleep(3000);
        WebElement allGames = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[3]/div[2]/div[2]/ul[16]/li[3]/a")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", allGames);
    }

    private void applyFiltersAndSort() {
        driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[2]/div[2]/div/div/div[2]/ul/li/span/a/span")).click();
        wait.until(ExpectedConditions.urlContains("free_shipping_eligible_1"));
        driver.findElement(By.id("p_n_condition-type/28071525031")).click();

        driver.findElement(By.xpath("/html/body/div[1]/div[1]/span/div/h1/div/div[4]/div/div/form/span/span/span/span")).click();
        driver.findElement(By.id("s-result-sort-select_2")).click();
    }

    private void addEligibleProductsToCart() throws InterruptedException {
        List<WebElement> priceElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='a-price-whole']")));
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < priceElements.size(); i++) {
            String text = priceElements.get(i).getText().replaceAll("[^0-9]", "");
            if (!text.isEmpty()) {
                int price = Integer.parseInt(text);
                if (price < 15000) {
                    validIndices.add(i);
                    countUnder15000++;
                    totalBelow15000 += price;
                }
            }
        }

        for (int index : validIndices) {
            priceElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[class='a-price-whole']")));
            if (index < priceElements.size()) {
                WebElement element = priceElements.get(index);
                element.click();
                WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
                addToCartButton.click();
                WebElement noThanks = wait.until(ExpectedConditions.elementToBeClickable(By.id("attachSiNoCoverage-announce")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", noThanks);
                Thread.sleep(2000);
                driver.navigate().back();
                driver.navigate().back();
            }
        }
    }

    private void validateCartAndCheckout() {
        WebElement goToCart = wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-cart")));
        goToCart.click();

        List<WebElement> quantityElements = driver.findElements(By.name("sc-quantity"));
        int countScQuantity = quantityElements.size();

        if (countScQuantity == countUnder15000) {
            System.out.println("All eligible products are added to cart");
        } else {
            throw new AssertionError("Validation Failed: Mismatch between products added to cart and eligible products");
        }
    }

    private void goToCheckoutAddAddress() {
        WebElement checkout = wait.until(ExpectedConditions.elementToBeClickable(By.name("proceedToRetailCheckout")));
        checkout.click();
//        WebElement addNewAddress = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[5]/div[1]/div/div/div/div[2]/div/div[5]/div[2]/div[2]/div/div/span/span/span/a")));
//        addNewAddress.click();
//        WebElement addName = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressFullName")));
//        addName.sendKeys("Ali Zikry");
//        WebElement addPhone = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressPhoneNumber")));
//        addPhone.sendKeys("1111111111");
//        WebElement addressLine1 = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressLine1")));
//        addressLine1.sendKeys("Talaat Harb Street");
//        WebElement building = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressLine2")));
//        building.sendKeys("Princess Tower");
//        WebElement city = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressCity")));
//        city.sendKeys("El Nozha");
//        WebElement district = wait.until(ExpectedConditions.elementToBeClickable(By.name("address-ui-widgets-enterAddressDistrictOrCounty")));
//        district.sendKeys("El-Nozha El-Gadida");
//        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(By.id("checkout-primary-continue-button-id-announce")));
//        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submit);
    }

    private void validateCheckoutTotalPrice() {
        WebElement ctaDecline = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[5]/div[1]/div/div/div/div/div/div/ms3-selection/div/div/div[3]/form/div[1]/div/div/a")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ctaDecline);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[5]/div[1]/div/div/div/div[1]/div/div[3]/div/ul/li[5]/span/div/div[2]")));
        String text = element.getText().trim();
        String numberOnly = text.replaceAll("[^0-9]", "");
        numberOnly = numberOnly.substring(0, numberOnly.length() - 2);
        int orderTotal = Integer.parseInt(numberOnly);

        System.out.println("totalBelow15000 " + totalBelow15000);
        System.out.println("orderTotal " + orderTotal);

        if (orderTotal == totalBelow15000) {
            System.out.println("Validation passed: Checkout total " + orderTotal + " is equal to the total of added products " + totalBelow15000);
        } else {
            System.out.println("Validation Failed: products total price added to cart (" + totalBelow15000 + ") is not equal to checkout price (" + orderTotal + ")");
            throw new AssertionError("Test Failed: Mismatch between products added to cart total price and products checkout price");
        }
    }

    @AfterTest
    public void quitBrowser() {
//        driver.quit();
    }
}
