import io.qameta.allure.Step;
import org.junit.jupiter.api.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class Tests {
    private static WebDriver driver;
    private static Actions action;


  
    @BeforeAll
    public static void setUp() {

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        action = new Actions(driver);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    }


    @Test
    public void testDragAndDrop() {
        openPage("https://the-internet.herokuapp.com/drag_and_drop");
        dragAndDrop("column-a", "column-b");
        verifyPosition("column-a", "B");
        verifyPosition("column-b", "A");
    }

    @Step("Открыть страницу {url}")
    private void openPage(String url) {
        driver.get(url);
    }

    @Step("Перетащить элемент {source} на элемент {target}")
    private void dragAndDrop(String sourceId, String targetId) {
        WebElement source = driver.findElement(By.id(sourceId));
        WebElement target = driver.findElement(By.id(targetId));
        action.clickAndHold(source)
                .moveToElement(target)
                .release()
                .perform();
    }

    @Step("Проверить, что элемент {elementId} теперь содержит текст {expectedText}")
    private void verifyPosition(String elementId, String expectedText) {
        WebElement element = driver.findElement(By.id(elementId)).findElement(By.tagName("header"));
        String actualText = element.getText();
        assertEquals(expectedText, actualText);
    }


    @Test
    public void testContextMenuAlertText() {
        openPage("https://the-internet.herokuapp.com/context_menu");
        rightClickOnContextMenu();
        verifyAlertText("You selected a context menu");
    }

    @Step("Вызвать контекстное меню")
    private void rightClickOnContextMenu() {
        WebElement contextBox = driver.findElement(By.id("hot-spot"));
        action.contextClick(contextBox).perform();
    }

    @Step("Проверить текст Alert")
    private void verifyAlertText(String expectedAlertText) {
        Alert alert = driver.switchTo().alert();
        String actualAlertText = alert.getText();
        alert.accept();
        assertEquals(expectedAlertText, actualAlertText);
    }


    @Test
    public void testScrollToText() {
        openPage("https://the-internet.herokuapp.com/infinite_scroll");
        scrollToText("eius");
    }

    @Step("Прокрутка страницы и поиск текста {text}")
    private void scrollToText(String text) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, 5);
        boolean found = false;
        while (!found) {
            try {

                WebElement textElement = driver.findElement(By.xpath("//*[contains(text(), '" + text + "')]"));
                jsExecutor.executeScript("arguments[0].scrollIntoView(true);", textElement);

                wait.until(ExpectedConditions.visibilityOf(textElement));
                found = true;
            } catch (Exception e) {
                jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
            }
        }
        assertTrue(found);
    }


    @Test
    public void testKeyPresses() {
        openPage("https://the-internet.herokuapp.com/key_presses");
        pressKeysAndVerify("A", "J"); // для символов от A до J
       pressSpecialKeysAndVerify(); // для специальных клавиш
    }

    @Step("Нажатие на символы от {start} до {end} и проверка результата")
    private void pressKeysAndVerify(String start, String end) {
        WebElement inputField = driver.findElement(By.id("target"));
        char startChar = start.charAt(0); // Преобразование строки в символ
        char endChar = end.charAt(0); // Преобразование строки в символ
        for (char ch = startChar; ch <= endChar; ch++) {
            action.sendKeys(inputField, String.valueOf(ch)).perform();
            assertResultText("You entered: " + ch);
        }
    }


    @Step("Нажатие на специальные клавиши и проверка результата")
    private void pressSpecialKeysAndVerify() {
        openPage("https://the-internet.herokuapp.com/key_presses");
        Keys[] specialKeys = {Keys.ENTER, Keys.CONTROL, Keys.ALT, Keys.TAB};
        String[] expectedTexts = {"You entered: ENTER", "You entered: CONTROL", "You entered: ALT", "You entered: TAB"};
        for (int i = 0; i < specialKeys.length; i++) {
            action.sendKeys(specialKeys[i]).perform();
            assertResultText(expectedTexts[i]);
        }
    }


    @Step("Проверка текста результата")
    private void assertResultText(String expectedText) {
        WebElement resultText = driver.findElement(By.id("result"));
        assertEquals(expectedText, resultText.getText());
    }


    @AfterAll
    public static void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
