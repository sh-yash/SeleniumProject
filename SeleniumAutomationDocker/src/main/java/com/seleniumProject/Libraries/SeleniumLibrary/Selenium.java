package com.seleniumProject.Libraries.SeleniumLibrary;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;

import com.seleniumProject.Runner.Controller;
import com.seleniumProject.Runner.Starter;
import com.seleniumProject.Utils.DeleteLogsAndReports;
import com.seleniumProject.Utils.Logging;
import com.seleniumProject.Utils.OneTime;
import com.seleniumProject.Utils.Xls_Reader;

/**
 * This class contains the methods to initialize the driver, launch the browser
 * and all the driver/browser related methods
 * 
 * @author Yash .
 * @version - Pre_dev_1.0
 *
 */
public class Selenium extends BaseUtility {
	public WebDriver driver;
	public static String browser = null;
	public String url = null;
	Wait<WebDriver> wait;
	Actions builder;
	Select select;
	private static Logging logger = new Logging();

	// private static ATUTestRecorder recorder;

	/**
	 * This method launches the browser and navigates to the url provided.
	 * 
	 * @param browserParam - browser name
	 * @param urlParam     - url to be navigated to
	 */
	public final String launchWebDriver(String browserParam, String urlParam) {
		browser = browserParam;
		url = urlParam;
		try {

			if (Starter.dicConfig.get("isParallel").equalsIgnoreCase("yes")
					|| Starter.dicConfig.get("isParallel").equalsIgnoreCase("y")) {
				url = Controller.getServerURL();
			}
			if (!url.toLowerCase().startsWith("http")) {
				url = "http://" + url;
			}
			logger.logInfo("Application URL: " + url);
			if (Starter.dicConfig.get("isDocker").equalsIgnoreCase("y")
					|| Starter.dicConfig.get("isDocker").equalsIgnoreCase("yes")) {
				launchDockerBrowser();
			} else if (Starter.dicConfig.get("isGrid").equalsIgnoreCase("y")
					|| Starter.dicConfig.get("isGrid").equalsIgnoreCase("yes")) {
				launchGridBrowser();
			}

			else {
				launchBrowser();
			}
			// maximizeWindow();
			logger.logInfo("Navigating to application url: " + url);
			driver.navigate().to(url);
			waitForPageLoad(60);
			isReady();
		} catch (RuntimeException e) {
			logger.logError(e.getMessage());
			teardownTest();
		}
		return url;
	}

	/**
	 * This method launches the browser
	 */
	private void launchBrowser() {
		try {

			String downloadFilepath = new File(".").getCanonicalPath() + "/Artifacts/OutFiles/";
			switch (browser.toString().toLowerCase()) {

			case "firefox":
				logger.logInfo("Setting firefox(gecko) driver path.");
				System.setProperty("webdriver.gecko.driver",
						new File(".").getCanonicalPath() + "/Drivers/geckodriver.exe");
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("intl.accept_languages", "en-gb");
				profile.setPreference("browser.download.dir", downloadFilepath);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
				FirefoxOptions options1 = new FirefoxOptions();
				options1.setProfile(profile);
				driver = new FirefoxDriver(options1);
				break;

			case "Edge":
				logger.logInfo("Setting firefox(gecko) driver path.");
				System.setProperty("webdriver.gecko.driver",
						new File(".").getCanonicalPath() + "/Drivers/msedgedriver.exe");
				EdgeOptions egoption = new EdgeOptions();
				driver = new EdgeDriver(egoption);
				break;

			case "phantom":
				logger.logInfo("Setting phantom driver path.");
				File src = new File(new File(".").getCanonicalPath() + "/Drivers/phantomjs.exe");
				System.setProperty("phantomjs.binary.path", src.getAbsolutePath());
				DesiredCapabilities caps = new DesiredCapabilities();
				caps.setJavascriptEnabled(true);
				caps.setCapability("locationContextEnabled", true);
				caps.setCapability("applicationCacheEnabled", true);
				caps.setCapability("browserConnectionEnabled", true);
				caps.setCapability("localToRemoteUrlAccessEnabled", true);
				caps.setCapability("locationContextEnabled", true);
				caps.setCapability("takesScreenshot", true);
				caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
						new File(".").getCanonicalPath() + "/Drivers/phantomjs.exe");
				driver = new PhantomJSDriver(caps);
				break;

			case "chrome":
				logger.logInfo("Setting chromedriver path.");
				System.setProperty("webdriver.chrome.driver",
						new File(".").getCanonicalPath() + "/Drivers/chromedriver.exe");
				System.setProperty("webdriver.http.factory", "apache");
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("download.default_directory", downloadFilepath);
				chromePrefs.put("profile.default_content_settings.cookies", "2");

				// chromePrefs.put("profile.default_content_settings.popups",
				// "0L");
				// chromePrefs.put("--disable-extensions", "disable-infobars");
				// chromePrefs.put("profile.default_content_setting_values.notifications",
				// 2);
				ChromeOptions options = new ChromeOptions();
				// options.setCapability(CapabilityType.BROWSER_VERSION, "57");
				options.setExperimentalOption("prefs", chromePrefs);
				driver = new ChromeDriver(options);
				break;
			default:
				break;
			}
			logger.logInfo("Set implicit wait - 3 seconds.");
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			maximizeWindow();
			// initiateVideoRecorder();

		} catch (Exception e) {

			logger.logError(e.getMessage());
			// teardownTest();
		}

	}

	/**
	 * @author qa-pc Vikas This method launches the grid browser
	 */
	private void launchGridBrowser() {

		DesiredCapabilities capability;
		try {

			InetAddress localhost = InetAddress.getLocalHost();
			String ip = localhost.getHostAddress();
			String nodeURL = "http://localhost:4444/wd/hub";

			switch (browser.toString().toLowerCase()) {
			case "firefox":
				logger.logInfo("Setting Firefox(gecko) driver path.");
				capability = DesiredCapabilities.firefox();
				capability.setBrowserName("firefox");
				capability.setPlatform(Platform.WIN10);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
				break;

			case "chrome":
				logger.logInfo("Setting Chrome Driver path.");
				logger.logInfo("Setting firefox(gecko) driver path.");
				capability = DesiredCapabilities.chrome();
				capability.setBrowserName("chrome");
				capability.setPlatform(Platform.WIN10);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
				break;

			case "internet explorer":
				logger.logInfo("Setting Internet Explorer driver path.");
				capability = DesiredCapabilities.internetExplorer();
				capability.setBrowserName("ie");
				capability.setPlatform(Platform.WIN10);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
				break;

			case "edge":
				logger.logInfo("Setting Edge driver path.");
				capability = DesiredCapabilities.edge();
				capability.setBrowserName("edge");
				capability.setPlatform(Platform.WIN10);
				driver = new RemoteWebDriver(new URL(nodeURL), capability);
				break;
			default:
				break;
			}
			logger.logInfo("Set implicit wait - 3 seconds.");
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			maximizeWindow();
			// initiateVideoRecorder();

		} catch (Exception e) {

			logger.logError(e.getMessage());
			// teardownTest();
		}

	}

	/**
	 * This method launches the browser
	 * 
	 * @param executionMode - Parallel/SingleInstance
	 */
	private void launchDockerBrowser() {
		try {
			String downloadFilepath = "/src/OutFiles/";
			switch (browser.toString().toLowerCase()) {

			case "firefox":
				FirefoxProfile profile = new FirefoxProfile();
				profile.setPreference("browser.download.dir", downloadFilepath);
				profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
						"application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
				DesiredCapabilities capability = new DesiredCapabilities();
				capability.setCapability(FirefoxDriver.PROFILE, profile);
				capability.setCapability("webdriver.log.driver", "INFO");
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
				break;
			case "chrome":
				HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
				chromePrefs.put("download.default_directory", downloadFilepath);
				// chromePrefs.put("profile.default_content_settings.popups",
				// "0L");
				// chromePrefs.put("--disable-extensions", "disable-infobars");
				// chromePrefs.put("profile.default_content_setting_values.notifications",
				// 2);
				// //1-Allow, 2-Block, 0-default
				ChromeOptions options = new ChromeOptions();
				// options.addArguments("--disable-notifications");
				options.setExperimentalOption("prefs", chromePrefs);
				DesiredCapabilities capability1 = DesiredCapabilities.chrome();
				capability1.setCapability(ChromeOptions.CAPABILITY, options);
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability1);
				break;
			default:
				break;
			}
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		} catch (Exception e) {

			logger.logError("" + e.getMessage());
			// launchDockerBrowser();
			// teardownTest();
		}
	}

	/**
	 * This method quits the driver
	 */
	public final void teardownTest() {
		try {
			logger.logInfo("TearDownTest, quitting driver.");
			if (driver != null) {
				driver.quit();
				driver = null;
			}
		} catch (RuntimeException e) {
			logger.logError(e.getMessage());
		}
	}

	/**
	 * Method to click on any element
	 * 
	 * @param element
	 */
	public final boolean click(WebElement element) {
		boolean flag = false;
		if (element == null)
			return flag;
		float startTime = getTime();
		float currTime = startTime;
		while (!flag && (currTime - startTime) < 15.0f) {
			try {
				if (waitForElementPresent(element, 5)) {
					element.click();
					flag = true;
					waitForElementPresent(null, 1);
					logger.logInfo("Clicked on given element.");
				}
			} catch (Exception e) {
				logger.logError(e.getMessage());
				flag = false;
			}
			currTime = getTime();
		}
		return flag;
	}

	/**
	 * Method to click on any element using JS
	 * 
	 * @param element
	 */
	public final boolean clickUsingJS(WebElement element) {
		boolean flag = false;
		try {
			((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
			waitForPageLoad(60);
			flag = true;
			logger.logInfo("Clicked on given element using JS.");
		} catch (Exception e) {
			logger.logError(e.getMessage());
			flag = false;
		}
		return flag;
	}

	/**
	 * Method to clear any element
	 * 
	 * @param element
	 */
	public final boolean clear(WebElement element) {
		boolean flag = false;
		try {
			element.clear();
			flag = true;
			logger.logInfo("Input field text clear successfully.");
		} catch (Exception e) {
			logger.logError(e.getMessage());
			flag = false;
		}
		return flag;
	}

	/**
	 * Method to double click at any element
	 * 
	 * @param element
	 */
	public final boolean doubleClick(WebElement element) {
		boolean flag = false;
		try {
			builder = new Actions(driver);
			builder.doubleClick(element).build().perform();
			flag = true;
		} catch (Exception e) {
			logger.logError(e.getMessage());
			flag = false;
		}
		return flag;
	}

	/**
	 * Method to wait for page load
	 * 
	 * @param int
	 */
	public final boolean waitForPageLoad(int timeInSeconds) {
		boolean flag = false;
		String strBrowserState = "";
		logger.logInfo("Waiting for page load.");
		int intTime = 0;
		try {
			while (!flag && intTime < timeInSeconds) {
				try {
					while (!((JavascriptExecutor) driver).executeScript("return navigator.onLine").toString()
							.equalsIgnoreCase("true") && intTime <= timeInSeconds) {
						waitForElementPresent(null, 1);
						intTime += 1;
					}
					flag = true;
				} catch (Exception e) {
					intTime += 1;
					waitForElementPresent(null, 1);
					flag = false;
				}
			}
			flag = false;
			intTime = 0;
			while (!strBrowserState.toLowerCase().equals("complete") && intTime <= timeInSeconds) {
				waitForElementPresent(null, 1);
				strBrowserState = ((JavascriptExecutor) driver).executeScript("return window.document.readyState")
						.toString();
				intTime += 1;
			}
			String strBrowserNavigatorState = "";
			strBrowserNavigatorState = ((JavascriptExecutor) driver).executeScript("return navigator.onLine").toString()
					.toLowerCase();
			if (!strBrowserState.toLowerCase().equals("complete") || !strBrowserNavigatorState.equals("true")) {
				logger.logInfo("Page did not reloaded.");
				flag = false;
			} else {
				flag = true;
				logger.logInfo("Page loaded successfully.");
			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		return flag;
	}

	/**
	 * Method to wait for element present
	 * 
	 * @param int
	 */
	// public final boolean waitForElementPresent(WebElement element, int
	// timeInSeconds) {
	// boolean flag = false;
	// logger.logInfo("Waiting for element present.");
	// try
	// {
	// for (int i = 0; i <= timeInSeconds; i++)
	// {
	// if (element!=null && (isDisplayed(element)||isEnabled(element)) )
	// {
	// flag = true;
	// break;
	// }
	// Thread.sleep(1000);
	// }
	// if (!flag) {
	// logger.logError("Element is not present on the WebPage even after waiting
	// for: "+timeInSeconds+ " seconds.");;
	// }
	// }
	// /*boolean flag = false;
	// logger.logInfo("Waiting for element present.");
	// try
	// {
	// wait = new FluentWait<WebDriver>(driver)
	// .withTimeout(timeInSeconds, TimeUnit.SECONDS)
	// .pollingEvery(timeInSeconds, TimeUnit.SECONDS)
	// .ignoring(NoSuchElementException.class).ignoring(TimeoutException.class);
	// if(element!=null)
	// {
	// if(wait.until(ExpectedConditions.elementToBeClickable(element)) != null
	// || !wait.until(ExpectedConditions.invisibilityOf(element))) {
	// flag=true;
	// }
	// else {
	// flag=false;
	// logger.logError("Element is not present on the WebPage even after waiting
	// for: "+timeInSeconds+ " seconds.");
	//
	// }
	// }
	// else {
	// driver.manage().timeouts().implicitlyWait(timeInSeconds,
	// TimeUnit.SECONDS);
	// }
	// }*/
	// catch (Exception e)
	// {
	// logger.logError("Unable to check whether element is present on the
	// web-page or not: "+e.getMessage());
	// }
	// return flag;
	// }

	public float getTime() {
		DateFormat df = new SimpleDateFormat("HH mm ss SSS");
		String mmss = df.format(new Date());
		return (float) Long.parseUnsignedLong(mmss.split(" ")[0]) * 3600
				+ Long.parseUnsignedLong(mmss.split(" ")[1]) * 60 + Long.parseUnsignedLong(mmss.split(" ")[2])
				+ Long.parseUnsignedLong(mmss.split(" ")[2]) * 0.001f;
	}

	public final WebElement fluentWaitToFindElementByXpath(String xpath, WebDriver driver) {
		WebElement ele = null;
		float startTime = getTime();
		float currTime = startTime;
		while ((currTime - startTime) < 15.0f) {
			try {
				ele = driver.findElement(By.xpath(xpath));
				return ele;
			} catch (Exception e) {
			}
			currTime = getTime();
		}
		return ele;
	}

	public final boolean waitForElementPresent(WebElement element, int timeInSeconds) {
		boolean flag = false;
		logger.logInfo("Waiting for element present.");
		try {
			float startTime = getTime();
			float currTime = startTime;
			while ((currTime - startTime) < (float) timeInSeconds) {
				if (element != null && (isDisplayed(element))) {
					flag = true;
					break;
				}
				currTime = getTime();
			}
			if (!flag) {
				logger.logError(
						"Element is not present on the WebPage even after waiting for: " + timeInSeconds + " seconds.");

			}
		} catch (Exception e) {
			logger.logError("Unable to check whether element is present on the web-page or not: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * Function to check whether element present on the webpage or not
	 * 
	 * @param element
	 * @return
	 */
	public final boolean isElementPresent(WebElement element) {
		boolean flag = false;
		logger.logInfo("Check whether element is present on the web-page or not.");
		try {
			if (isDisplayed(element)) {
				flag = true;
				return flag;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
			logger.logError("Unable to check whether element is present on the web-page or not: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * Method to mouse hover on any element
	 * 
	 * @param element
	 */
	public final boolean mouseHover(WebElement element, WebDriver driver) {
		boolean flag = false;
		try {
			builder = new Actions(driver);
			builder.moveToElement(element).build().perform();
			logger.logInfo("Cursor moved to element.");
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.logError(e.getMessage());
		}
		return flag;
	}

	/**
	 * Method to type on any textbox
	 * 
	 * @param element
	 * @param strKeyword
	 */
	public final boolean sendkeys(WebElement element, String strKeyword) {
		boolean flag = false;
		try {
			element.sendKeys(strKeyword);
			flag = true;
			logger.logInfo("'" + strKeyword + "' - text send to input field.");
		} catch (Exception e) {
			flag = false;
			logger.logError(e.getMessage());
		}
		return flag;
	}

	/**
	 * Method to select any element
	 * 
	 * @param strListValue - the text value to be selected
	 * @param element
	 */
	public final boolean selectByText(WebElement element, String strListValue) {
		boolean flag = false;
		try {
			select = new Select(element);
			select.selectByVisibleText(strListValue);
			flag = true;
			logger.logInfo("Text selected - " + strListValue);
		} catch (Exception e) {
			flag = false;
			logger.logError(e.getMessage());
		}
		return flag;
	}

	/**
	 * Method to select any element
	 * 
	 * @param strListValue - the value to be selected
	 * @param element
	 */
	public final void selectByValue(WebElement element, String strListValue) {
		try {
			select = new Select(element);
			select.selectByValue(strListValue);
			logger.logInfo("Element selected by value.");
		} catch (Exception e) {
			logger.logError("Unable to select element by Value: " + e.getMessage());
		}
	}

	/**
	 * Method to select any element
	 * 
	 * @param index   - the value at a index to be selected
	 * @param element
	 */
	public final void selectByIndex(WebElement element, int index) {
		try {
			select = new Select(element);
			select.selectByIndex(index);
			logger.logInfo("Element selected by Index.");
		} catch (Exception e) {
			logger.logError("Unable to select element by Index: " + e.getMessage());
		}
	}

	/**
	 * This method checks if an element is displayed or not
	 * 
	 * @param element - element which is to be checked for display
	 */
	public final boolean isDisplayed(WebElement element) {
		boolean flag = false;
		try {
			flag = element.isDisplayed();
			logger.logInfo("Checked whether element is displayed: " + flag);
		} catch (Exception e) {
			logger.logError("Unable to check whether element is displayed or not: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method press over any element
	 * 
	 * @param element - element on which enter is to be pressed
	 */
	public final void pressEnter(WebElement element) {
		try {
			element.sendKeys(Keys.RETURN);
			logger.logInfo("Simulated press 'ENTER' key.");
		} catch (Exception e) {
			logger.logError("Unable to simulate 'ENTER': " + e.getMessage());
		}
	}

	/**
	 * This method pressed control and enter
	 * 
	 * @param element
	 */
	public final void ctrlPlusEnter(WebElement element) {
		try {
			String keysPressed = Keys.chord(Keys.CONTROL, Keys.RETURN);
			element.sendKeys(keysPressed);
			logger.logInfo("Simulated press 'CTRL + ENTER' key");
		} catch (Exception e) {
			logger.logError("Unable to simulate CTRL + ENTER: " + e.getMessage());
		}
	}

	/**
	 * This method presses tab
	 * 
	 * @param element
	 */
	public final void tab(WebElement element) {
		try {
			element.sendKeys(Keys.TAB);
			logger.logInfo("Send 'TAB' key.");
		} catch (Exception e) {
			logger.logError("Unable to simulate 'TAB'");
		}
	}

	/**
	 * This method verifies is the element is not selected
	 * 
	 * @param element
	 */
	public final boolean isNotSelected(WebElement element) {
		boolean flag = false;
		try {
			flag = !element.isSelected();
			logger.logInfo("Check whether check box is not-selected: " + flag);
		} catch (Exception e) {
			logger.logError("Unable to check check-box staus: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method verifies is the element is enabled
	 * 
	 * @param element
	 */
	public final boolean isEnabled(WebElement element) {
		boolean flag = false;
		try {
			flag = element.isEnabled();
			logger.logInfo("Element status(Enabled or not): " + flag);
		} catch (Exception e) {
			logger.logError("Unable to check whether element is enabled or not: " + e.getMessage());
		}
		return flag;
	}

	public void waitUntilLoadFinish(WebDriver driver) {
		waitForPageLoad(4);
		while (driver.findElements(By.xpath(
				"//*[(@id='actual-content-body' and (descendant::span[contains(text(),'Loading')] or descendant::div[contains(text(),'Loading')])) or contains(@class,'loading') or contains(@class,'block-ui')]"))
				.size() > 0)
			;
	}

	/**
	 * This method verifies is the element is selected
	 * 
	 * @param element
	 */
	public final boolean isSelected(WebElement element) {
		boolean flag = false;
		try {
			flag = element.isSelected();
			logger.logInfo("Status of checkbox: " + flag);
		} catch (Exception e) {
			logger.logError("Unable to check status of check-box: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method gets the text of the element
	 * 
	 * @param element
	 * @return text on the element
	 */
	public final String getText(WebElement element) {
		String elementText = null;
		try {
			elementText = element.getText();
			logger.logInfo("Fetched text of element: " + elementText);
		} catch (Exception e) {
			logger.logError("Unable to get text of element: " + e.getMessage());
		}
		return elementText;
	}

	/**
	 * this method gets the value of the element
	 * 
	 * @param element
	 * @return - Attribute value
	 */
	public final String getValue(WebElement element) {
		String elementValue = "";
		try {
			elementValue = element.getAttribute("value");
			logger.logInfo("Fetched value of element: " + elementValue);
		} catch (Exception e) {
			logger.logError("Unable to get value of element: " + e.getMessage());
		}
		return elementValue;
	}

	/**
	 * This method checks if the document is in ready state
	 */
	public final void isReady() {
		((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("if (document.readyState) return document.readyState;");
		logger.logInfo("Web page is in ready state.");
	}

	/**
	 * This selects the checkbox
	 * 
	 * @param element
	 */
	public final boolean selectCheckbox(WebElement element) {
		boolean flag = false;
		try {
			if (!element.isSelected()) {
				element.click();
				flag = true;
				logger.logInfo("Check-box selected.");
			} else {
				flag = true;
				logger.logInfo("Check-box already selected.");
			}
		} catch (Exception e) {
			flag = false;
			logger.logError("Unable to select checkbox: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This deselcts the checkbox
	 * 
	 * @param element
	 * @return true/false
	 */
	public final boolean deSelectCheckbox(WebElement element) {
		boolean flag = false;
		try {
			if (element.isSelected()) {
				element.click();
				flag = true;
				logger.logInfo("De-selected checkbox.");
			} else {
				flag = true;
				logger.logInfo("Element is not selected.");
			}
		} catch (Exception e) {
			flag = false;
			logger.logError("Unable to de-select checkbox: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method moves to the element in the list
	 * 
	 * @param xpathOfElement
	 */
	public final void moveToElementInList(String xpathOfElement) {
		try {
			Actions builder = new Actions(driver);
			builder.moveToElement(driver.findElement(By.xpath(xpathOfElement))).build().perform();
			logger.logInfo("Cursor move to element");
		} catch (Exception e) {
			logger.logError("Unable to move to element: " + e.getMessage());
		}

	}

	/**
	 * Method to press escape button
	 */
	public final void pressEsc() {
		try {
			builder = new Actions(driver);
			builder.sendKeys(Keys.ESCAPE).build().perform();
			logger.logInfo("Simulated 'Esc' press button.");
		} catch (Exception e) {
			logger.logError("Unable to simulate 'Esc' button: " + e.getMessage());
		}
	}

	/**
	 * Method to press escape button
	 * 
	 * @param element
	 */
	public final boolean getStatusofCheckBox(WebElement element) {
		boolean status = false;
		try {
			status = element.isSelected();
			logger.logInfo("Status of checkbox fetched: " + status);
		} catch (Exception e) {
			logger.logError("Unable to fetch checkbox status: " + e.getMessage());
		}
		return status;
	}

	/**
	 * Method to get the value of the 'title' attribute
	 * 
	 * @param element
	 */
	public final String getAttributeValue(WebElement element) {
		String attributevalue = null;
		float startTime = getTime();
		float currTime = startTime;
		while (attributevalue == null && (currTime - startTime) < 10.0f) {
			try {
				attributevalue = element.getAttribute("title");
				logger.logInfo("Attribute value fethced: " + attributevalue);
			} catch (Exception e) {
				logger.logError("Unable to fetch attribute value: " + e.getMessage());
			}
			currTime = getTime();
		}
		return attributevalue;
	}

	/**
	 * This method navigates to the previous page in browser
	 */
	public final void back(WebDriver driver) {
		try {
			driver.navigate().back();
			logger.logInfo("Navigate to previous page.");
		} catch (Exception e) {
			logger.logError("Unable to move back: " + e.getMessage());
		}
	}

	/**
	 * This method navigates forward in browser
	 */
	public final void forward() {
		try {
			driver.navigate().forward();
			logger.logInfo("Page forwarded.");
		} catch (Exception e) {
			logger.logError("Unable to forward page: " + e.getMessage());
		}
	}

	/**
	 * This method navigates to the specified url in the browser
	 * 
	 * @param url
	 */
	public final void navigate(String strURL) {
		try {
			driver.navigate().to(strURL);
			logger.logInfo("Navigated to: " + strURL);
		} catch (Exception e) {
			logger.logError("Unable to navigate to specified url: " + e.getMessage());
		}
	}

	/**
	 * This method refreshes the browser
	 */
	public final void pageRefresh() {
		try {
			driver.navigate().refresh();
			logger.logInfo("Page refreshed.");
		} catch (Exception e) {
			logger.logError("Unable to refresh page: " + e.getMessage());
		}
	}

	/**
	 * This method maximise the browser
	 */
	public final void maximizeWindow() {
		try {
			driver.manage().window().maximize();
			logger.logInfo("Browser maximized.");
		} catch (Exception e) {
			logger.logError("Unable to maximize browser: " + e.getMessage());
		}
	}

	/**
	 * This method fullscreen the browser
	 */
	public final void fullscreenWindow() {
		try {
			driver.manage().window().fullscreen();
			logger.logInfo("Browser is in Fullscreen mode.");
		} catch (Exception e) {
			logger.logError("Unable to fullscreen browser: " + e.getMessage());
		}
	}

	/**
	 * This method will check whether new tab of expected URL/TITLE is present or
	 * not
	 * 
	 * @param driver
	 * @param option
	 * @param expectedResult
	 * @return
	 */
	public boolean isGivenNewWindowPresent(WebDriver driver, String option, String expectedResult) {
		boolean flag = false;
		String originalWindowHandle = driver.getWindowHandle();
		Set<String> windowHandles = driver.getWindowHandles();
		if (windowHandles.size() > 1) {
			for (String window : windowHandles) {
				if (!window.equals(originalWindowHandle)) {
					driver.switchTo().window(window);
					switch (option.toLowerCase()) {
					case "url":
						String url = driver.getCurrentUrl();
						if (url.contains(expectedResult)) {
							flag = true;
							driver.close();
							break;
						}
						break;
					case "title":
						String title = driver.getTitle();
						if (title.contains(expectedResult)) {
							flag = true;
							driver.close();
							break;
						}
						break;
					}

				}

			}
			driver.switchTo().window(originalWindowHandle);
		}
		return flag;
	}

	/**
	 * This method switches to the new window
	 * 
	 * @param strWindowTitle
	 */
	public final boolean switchToNewWindow(String strWindowTitle) {
		boolean flag = false;
		try {
			driver.switchTo().window(strWindowTitle);
			logger.logInfo("Switched to new window.");
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.logError("Unable to switch new window: " + e.getMessage());
		}
		return flag;
	}

	/**
	 * This method gets the URL
	 */
	public final String getURL() {
		String strURL = "";
		try {
			strURL = driver.getCurrentUrl();
			logger.logInfo("Got the url of application");
		} catch (Exception e) {
			logger.logError("Unable to get URL of application. " + e.getMessage());
		}
		return strURL;
	}

	/**
	 * This method verifies if particular link is navigating to its page or not
	 */
	public boolean verifyURL(WebDriver driver, String str) {
		logger.logInfo("Verifying URL contains " + str);
		// String strCurrentURL = driver.getCurrentUrl();

		String strCurrentURL = getURL();

		if (!strCurrentURL.contains(str) && !strCurrentURL.equals(str)) {
			return false;
		}
		return true;
	}

	/**
	 * this method verifies the window title
	 */
	public final String getTitle() {
		String strBrowserTitle = "";
		try {
			strBrowserTitle = driver.getTitle();
			logger.logInfo("Got the title of webpage.");
		} catch (Exception e) {
			logger.logError("Unable to get Title of web page. " + e.getMessage());
		}
		return strBrowserTitle;
	}

	public boolean verifyTitle(String expectedTitle) {
		boolean flag = false;
		String actualTitle = driver.getTitle();
		try {
			logger.logInfo("Title Assertion");
			Assert.assertEquals(actualTitle, actualTitle);
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.logInfo("Error in title assertion");
		}
		return flag;

	}

	/**
	 * This closes the driver
	 */
	public final void close() {
		try {
			driver.close();
			logger.logInfo("Driver closed successfully.");
		} catch (Exception e) {
			logger.logError("Exception in closing driver: " + e.getMessage());
		}
	}

	/**
	 * This method deletes the cookies
	 */
	public final void deleteCookies() {
		try {
			driver.manage().deleteAllCookies();
			logger.logInfo("Cookies deleted.");
		} catch (Exception e) {
			logger.logError("Exception occurred while deleting cookies. " + e.getMessage());
		}
	}

	/**
	 * This method gets the element from its locator
	 * 
	 * @param elementLocator
	 * @param parentElement
	 * @return true/false
	 */
	@SuppressWarnings("null")
	public final List<WebElement> getElements(WebDriver driver, String elementLocator, WebElement parentElement) {
		List<WebElement> elements = null;
		String property, propertyValue = "";
		try {
			// If identification is in the format of XPATH, enter this if block
			if (elementLocator.substring(0, 2).equals("//") || elementLocator.substring(0, 3).equals("(//")
					|| elementLocator.substring(0, 4).equals("((//")) {
				try {
					if (parentElement == null) {
						elements.add(driver.findElement(By.xpath(elementLocator)));
					} else {
						elements.add(parentElement.findElement(By.xpath(elementLocator)));
					}
					logger.logInfo("Element found by xpath.");
				} catch (RuntimeException e) {
					logger.logError("Throwing runtime exception: Object is not present on page.");
					throw new RuntimeException(

							"Object " + elementLocator + " is not present on page." + e.getMessage());
				}

			} else // If identification is not in format of xpath, then enter
					// this else block
			{
				try { // Split the identification, if it does not contain '=' ,
						// then Split('=')[1]
						// will throw an exception that locator(identification)
						// is not in correct format
					property = elementLocator.split("[=]", -1)[0];
					propertyValue = elementLocator.split("[=]", -1)[1];
				} catch (RuntimeException e) {
					logger.logError("Locator is not in proper format. Unable to find property and property value.");
					throw new RuntimeException("Locator '" + elementLocator + "' is not in proper format");
				}
				try { // If identification is not in format of xpath, then enter
						// this block
					switch (property.toLowerCase()) {
					case "id":
						if (parentElement == null)
							elements.add(driver.findElement(By.id(propertyValue)));
						else
							elements.add(parentElement.findElement(By.id(propertyValue)));
						break;
					case "class":
						if (parentElement == null)
							elements.add(driver.findElement(By.className(propertyValue)));
						else
							elements.add(parentElement.findElement(By.className(propertyValue)));
						break;
					case "name":
						if (parentElement == null)
							elements.add(driver.findElement(By.name(propertyValue)));
						else
							elements.add(parentElement.findElement(By.name(propertyValue)));
						break;
					case "css":
						if (parentElement == null)
							elements.add(driver.findElement(By.cssSelector(propertyValue)));
						else
							elements.add(parentElement.findElement(By.cssSelector(propertyValue)));
						break;
					case "link":
						if (parentElement == null)
							elements.add(driver.findElement(By.linkText(propertyValue)));
						else
							elements.add(parentElement.findElement(By.linkText(propertyValue)));
						break;
					default:
						logger.logError("Locator is not in proper format.");
						throw new RuntimeException("Locator '" + elementLocator + "' is not in proper format.");

					}
				} catch (RuntimeException e) {
					logger.logError("Throwing runtime exception: Object is not present on page.");
					throw new RuntimeException("Object " + elements + " is not present on page." + e.getMessage());
				}
			}
		} catch (RuntimeException e) {
			logger.logError(e.getMessage());
		}
		logger.logInfo("Element found, returning element.");
		return elements;
	}

	/**
	 * This method executes the java script
	 * 
	 * @param strJavascript - java script function to be executed.
	 * @return true/false
	 */
	public final boolean executeJavascript(String strJavascript) {
		boolean flag = false;
		try {
			((JavascriptExecutor) driver).executeScript(strJavascript);
			flag = true;
			logger.logInfo("Javascript executed successfully.");
		} catch (Exception e) {
			flag = false;
			logger.logError("Failed to execute Javascript. " + e.getMessage());

		}
		return flag;
	}

	/**
	 * This method gets the element count(useful for a list/table type property)
	 * 
	 * @param xpath of the elementProperty
	 * @return number of elements in the list/table
	 */

	public final int getElementCount(String elementProperty) {
		int elementsCount = 0;
		try {
			elementsCount = driver.findElements(By.xpath(elementProperty)).size();
			logger.logInfo("Got elements: " + elementsCount);
		} catch (Exception e) {
			logger.logError("Failed in getting element count: " + e.getMessage());
		}
		return elementsCount;
	}

	/**
	 * This method will take screenshot and save at given location
	 * 
	 * @param driver
	 * @param fileName
	 * @param directoryName
	 * @return
	 */
	public final String saveScreenShot(WebDriver driver, String fileName, String directoryName) {
		String screenshotFilePath = null;
		screenshotFilePath = directoryName + "/" + fileName;
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(screenshot, new File(screenshotFilePath));
			logger.logInfo("Screenshot taken and saved. " + fileName);
		} catch (IOException e) {
			logger.logError("Failed in taking screenshot. " + e.getMessage());
			return null;
		}
		return fileName;
	}

	/**
	 * 
	 * This Method is used to get the scroll position
	 */
	public final long getScrollPosition(WebDriver driver) {
		try {
			logger.logInfo("Getting scroll bar position in the webpage.");
			return (long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset;");
		} catch (Exception e) {
			logger.logInfo("Could not get scroll bar position");
			return -1;
		}
	}

	/**
	 * 
	 * This Method is used to get the position
	 */
	public final Point getElementPosition(WebElement element) {
		try {
			logger.logInfo("Getting element position.");
			return element.getLocation();
		} catch (Exception e) {
			logger.logInfo("Could not get element position.");
			return new Point(-1, -1);
		}
	}

	/**
	 * @author Vikas This Method is used to Drag and Drop value by target
	 * 
	 * @param element : Time to provide wait
	 * @param source  : source where element to be drag and drop
	 * @param target  : target where element to be drag and drop
	 */
	public boolean dragAndDropByPosition(WebElement source, WebElement target) {
		boolean flag = false;
		builder = new Actions(driver);
		try {
			logger.logInfo("Performing element drag and drop operation.");
			builder.dragAndDrop(source, target).perform();
			flag = true;
		} catch (Exception e) {
			logger.logError("Could not performing drag and drop operation." + e.getMessage());
			flag = false;
		}
		return flag;

	}

	/**
	 * @author Vikas This method is used to scroll the web page to a particuller Web
	 *         Element
	 * 
	 * @param locator - Must be valid Xpath or WebElement
	 * @param choice  - Choice Must be a valid "WebElement" or "xpath"
	 * @return : True if element is present
	 */
	public boolean scrollByVisibleElement(WebElement element) throws Exception {
		boolean flag = false;
		try {
			logger.logInfo("Performing element to visible element.");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView(true);", element);
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.logError("Error during scroll ." + e.getMessage());

		}
		return flag;
	}

	/**
	 * @author Vikas This method is used to Scroll down the full page
	 */
	public boolean scrollFullPage() {
		boolean flag = false;
		try {
			logger.logInfo("Scrolling full page");
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			flag = true;
		} catch (Exception e) {
			flag = false;
			logger.logError("Something went wrong page scroll" + e.getMessage());
		}
		return flag;
	}

	/**
	 * @author Vikas
	 * This Method is used to copy the text
	 */

	public ArrayList<String> getTotalWebLink() {
		ArrayList<String> linklist = new ArrayList<String>();
		String linkName = "";
		try {
			List<WebElement> link = driver.findElements(By.tagName("a"));
			for (WebElement webElement : link) {
				linkName = webElement.getText();
				linklist.add(linkName);
				logger.logInfo("Total Number of link present on the page");
				logger.logInfo(webElement.getText() + " - " + webElement.getAttribute("href"));
			}
		} catch (Exception e) {
			logger.logError("Something went wrong links are not find");
		}
		return linklist;

	}

	/**
	 * @author Vikas This method is used to Save the data in a File
	 * 
	 * @param fileName : Data to be save in which file
	 * @param data     : Data to be saved in file
	 */
	public void saveResultInFile(String fileName, String data) {
		String path = System.getProperty("user.dir").concat("\\Artifacts\\InFiles\\");
		File log = new File(path + fileName + ".txt");
		try {
			if (log.exists() == false) {
				log.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(data + "" + " , " + "  " + " ");
			bufferedWriter.close();
			logger.logInfo("Data write in file successfully");
		} catch (IOException e) {
			logger.logError("COULD NOT WRITE!!" + e.getMessage());

		}
	}

	/**
	 * This method is used to read the data from the Excel File
	 * 
	 * @param ExcelName - Must be valid Excel Name present in the system
	 * @param testcase  - Must be valid sheet name whose test cases need to be
	 *                  Executes
	 */
	public static Object[][] getExcelData(String excelSheetName, String testcase) {
		try {

			String path = new File(".").getCanonicalPath() + "/Artifacts/InFiles/";
			String excelPath = path + excelSheetName;
			logger.logInfo("Reading Data from the excel file");
			Xls_Reader xlsData = new Xls_Reader(excelPath);

			int rowNum = xlsData.getRowCount(testcase);
			System.out.println(rowNum);
			int colNum = xlsData.getColumnCount(testcase);
			Object[][] excelData = new Object[rowNum - 1][colNum];
			for (int i = 2; i <= rowNum; i++) {
				for (int j = 0; j < colNum; j++) {
					excelData[i - 2][j] = xlsData.getCellData(testcase, j, i);
				}
			}
			return excelData;
		} catch (IOException e) {
			throw new RuntimeException("Unable to fetch data from execel file");
		}

	}

	/**
	 * This method is used to Find the broken link in a URL
	 * 
	 * @param WebURL : Web URL whose broken link to be find
	 */

	public void findBrokenLinkInPage(String WebURL) {
		driver.get(WebURL);
		HttpURLConnection huc = null;
		int respCode = 200;
		String URL = "";
		String homePage = driver.getCurrentUrl();

		List<WebElement> links = driver.findElements(By.tagName("a"));
		Iterator<WebElement> it = links.iterator();

		while (it.hasNext()) {
			URL = it.next().getAttribute("href");
			System.out.println(URL);
			if (URL == null || URL.isEmpty()) {
				System.out.println("URL is either not configured for anchor tag or it is empty");
				continue;
			}

			if (!URL.startsWith(homePage)) {
				System.out.println("URL belongs to another domain, skipping it.");
				continue;
			}

			try {
				huc = (HttpURLConnection) (new URL(URL).openConnection());
				huc.setRequestMethod("HEAD");
				huc.connect();
				respCode = huc.getResponseCode();

				if (respCode >= 400) {
					logger.logInfo(URL + " is a broken link");
				} else {
					logger.logInfo(URL + " is a valid link");
				}

			} catch (MalformedURLException e) {
				System.out.println("Some error in getting code");
				logger.logError("" + e.getMessage());

			} catch (IOException e) {
				System.out.println("Some error in getting code block 2");
				logger.logError("" + e.getMessage());
			}
		}
	}

}
