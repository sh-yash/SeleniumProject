package com.seleniumProject.Utils;

import java.io.IOException;

import org.testng.ITestResult;
import org.testng.annotations.BeforeClass;

/**
 * 
 * @author Yash .
 *
 */
public interface IBeforeAfter {

	/**
	 * The method is used to initiate browser and should have annotation
	 * <ul>
	 * <li>@BeforeTest</li>
	 * <li>@Parameters(value = { "module", "serverIP", "browser" })</li>
	 * </ul>
	 * 
	 */
	@BeforeClass
	void initiateBrowser(String module, String serverIP, String browser, String clienv);

	/**
	 * The login and test method is called here and should have annotation
	 * <ul>
	 * <li>@Test</li>
	 * </ul>
	 */
	void performTest();

	/**
	 * The method will log result for failed or skipped @Test methods and should
	 * have annotation
	 * <ul>
	 * <li>@AfterMethod</li>
	 * </ul>
	 */
	void getResult(ITestResult result) throws IOException;

	/**
	 * This method will logout and will revert back the changes if made by the
	 * test script is called here and should have annotation
	 * <ul>
	 * <li>@AfterClass</li>
	 * </ul>
	 */
	void performLogoutandRevertChanges();

	/**
	 * The method will close browser instance and should have annotation
	 * <ul>
	 * <li>@AfterTest</li>
	 * </ul>
	 */
	void closeBrowser();
	
     
	

}
