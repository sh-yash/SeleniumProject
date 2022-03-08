package DesignSuite;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

import com.seleniumProject.Libraries.SeleniumLibrary.Selenium;

/**
 * 
 * @author yash
 *
 */
public class GoogleSearchPage extends Selenium {

	// Returns element for user on top right
	@FindBy(how = How.CSS, using = ".usersEmailAddress")
	private WebElement userEmail;

	// Reservations tab
	@FindBy(how = How.XPATH, using = "(//span[normalize-space(text())='Reservations'])")
	private WebElement reservationsTab;

	// System Tab
	@FindBy(how = How.XPATH, using = "(//span[normalize-space(text())='System'])")
	private WebElement systemTab;

	// Manage your account link in email dropdown menu
	@FindBy(how = How.LINK_TEXT, using = "Manage your account")
	private WebElement manageYourAccountLink;

	// Users Tab
	@FindBy(how = How.XPATH, using = "(//span[normalize-space(text())='Users'])")
	private WebElement usersTab;

	// Application Tab
	@FindBy(how = How.XPATH, using = "(//span[normalize-space(text())='Applications'])")
	private WebElement applicationTab;

	// Device Tab
	@FindBy(how = How.XPATH, using = "//a[@href='#/Device/Index']")
	private WebElement deviceTab;

	// Select Download Cli from dropdown
	@FindBy(how = How.LINK_TEXT, using = "Download GigaFox CLI")
	private WebElement downloadCli;

	// Windows cli link
	@FindBy(how = How.LINK_TEXT, using = "Download dc-win-x86.zip")
	private WebElement wincli;

	// close button link
	@FindBy(how = How.XPATH, using = "//button[@class='close']")
	private WebElement closeButton;

	@FindBy(how = How.XPATH, using = "//a[@class='editOwnUser']")
	private WebElement editUser;

	// close button in about box
	@FindBy(how = How.XPATH, using = "//button[text()='Close']")
	private WebElement closeButtonAboutBox;

	// About ELement in the foooter
	@FindBy(how = How.XPATH, using = "//div[@class='ex-about']/a[text()='About']")
	private WebElement aboutFooter;

	// About Box Content
	@FindBy(how = How.XPATH, using = "//div[@class='modal-body']")
	private WebElement aboutBoxContent;

	/**
	 * Method to Click Applications Tab.
	 * 
	 * @return True/false if click successful
	 * @author Yash.
	 */
	public boolean clickAppTab() {
		if (waitForElementPresent(applicationTab, 3)) {
			return click(applicationTab);
		} else {
			return false;
		}
	}

	/**
	 * Method to Click Close PoP-Up When clicked on any download button..
	 * 
	 * @return True/false if click successfull
	 * @author Yash
	 */

	public boolean clickClosePopUp() {
		if (waitForElementPresent(closeButton, 5)) {
			return click(closeButton);
		} else {
			return false;
		}
	}

	public String getUserName() {
		return getText(userEmail);
	}

	/**
	 * Method to Click Devices Tab.
	 * 
	 * @return True/false if click successful
	 * @author Yash.
	 */
	public boolean clickDevicesTab() {
		if (waitForElementPresent(deviceTab, 3)) {
			return click(deviceTab);
		} else {
			return false;
		}
	}

	// Define Click on Reservation Tab
	public boolean clickReservationsTab() {
		if (waitForElementPresent(reservationsTab, 1)) {
			return click(reservationsTab);
		} else {
			return false;
		}
	}

	/**
	 * Method to <Download Cli zip file for windows from UI and Extract the
	 * same.>
	 * 
	 * @param zipFilePath
	 *            - Downloaded zip file path
	 * @param destDir
	 *            - Extracted file path .
	 * @author Yash
	 * @return boolean
	 * @throws Exception
	 *             -When download failed or unable to extract.
	 */
	public boolean downloadWindowsCli(String zipFilePath, String destDir) throws Exception {
		if (waitForElementPresent(userEmail, 5)) {
			click(userEmail);
			waitForElementPresent(null, 2);
			if (waitForElementPresent(downloadCli, 5)) {
				click(downloadCli);
				waitForElementPresent(null, 2);
				if (waitForElementPresent(wincli, 5)) {
					click(wincli);
					waitForElementPresent(null, 2);
					waitForElementPresent(closeButton, 3);
					click(closeButton);

					waitForFileDownload();
				} else
					throw new RuntimeException("Unable to click Windows cli in pop up");
			} else {
				throw new RuntimeException("unnable to click Download cli from dropdown");
			}
		} else {
			throw new RuntimeException("Unable to click UserEmail");
		}
		return true;
	}

	// Define Click on System Tab
	public boolean clickSystemTab() {
		if (waitForElementPresent(systemTab, 3)) {
			return click(systemTab);
		} else {
			return false;
		}
	}

	// Define Click on Users Tab
	public boolean clickUsersTab() {
		if (waitForElementPresent(usersTab, 3)) {
			return click(usersTab);
		} else {
			return false;
		}
	}

	public boolean clickEditUser() {
		if (waitForElementPresent(userEmail, 3)) {
			click(userEmail);
			if (waitForElementPresent(editUser, 2)) {
				return click(editUser);
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean clickUsersEmailAddressLink() {
		if (waitForElementPresent(userEmail, 10)) {
			return click(userEmail);
		}
		return false;
	}

	public boolean clickManageYourAccountLink() {
		if (waitForElementPresent(manageYourAccountLink, 10)) {
			return click(manageYourAccountLink);
		}
		return false;
	}

	public boolean clickAboutFooter() {
		if (waitForElementPresent(aboutFooter, 3)) {
			return click(aboutFooter);
		}
		return false;
	}

	public boolean clickCloseAboutBox() {
		if (waitForElementPresent(closeButtonAboutBox, 2)) {
			return click(closeButtonAboutBox);
		}
		return false;
	}

	public boolean verifyAboutBoxContent() {
		try {
			if (!clickAboutFooter()) {
				throw new RuntimeException("Unable to click the about in footer");
			}
			while (true) {
				if (isElementPresent(aboutBoxContent))
					break;
				continue;
			}
			if (aboutBoxContent.getText().contains("Build version:") || aboutBoxContent.getText().contains("Node key:")
					|| aboutBoxContent.getText().contains("System:")
					|| aboutBoxContent.getText().contains("Hardware serial:")) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	}

}
