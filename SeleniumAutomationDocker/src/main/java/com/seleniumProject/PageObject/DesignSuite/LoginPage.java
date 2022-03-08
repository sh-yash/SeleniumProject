package DesignSuite;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.seleniumProject.Libraries.SeleniumLibrary.Selenium;

public class LoginPage extends Selenium {

	// @FindBy(how = How.XPATH, using =
	// "//div[contains(@class,'errorReportContainer')]/button")

	@FindBy(how = How.XPATH, using = "//input[@placeholder='Username']")
	private WebElement username;

	@FindBy(how = How.XPATH, using = "//input[@placeholder='Password']")
	private WebElement password;

	@FindBy(how = How.XPATH, using = "//img[@id='img-login']")
	private WebElement loginbutton;

	@FindBy(how = How.XPATH, using = "//img[contains(@src,'LOGO')]")
	private WebElement logo;

	public boolean verifyseleniumProjectLogo() {
		try {
			if (waitForElementPresent(logo, 5)) {
				return isDisplayed(logo);

			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	public boolean login(String userna, String pass) {

		try {
			if (waitForElementPresent(username, 5)) {
				username.sendKeys(userna);
				System.out.println("Enter the username");
				if (waitForElementPresent(password, 5)) {
					password.sendKeys(pass);
					System.out.println("Enter the Password");
					if (waitForElementPresent(loginbutton, 5)) {
						loginbutton.click();
						System.out.println("Succesfully Login in Design Room");
						return true;
					} else {
						System.out.println("Login button does not clicked");
						return false;
					}
				} else {
					System.out.println("password doesnot entered");
					return false;
				}
			} else {
				System.out.println("Username doesnot entered");
				return false;
			}

		} catch (Exception e) {
			return false;
		}

	}
}
