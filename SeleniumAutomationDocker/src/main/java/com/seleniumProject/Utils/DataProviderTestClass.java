package com.seleniumProject.Utils;

import org.testng.annotations.DataProvider;

import com.seleniumProject.Libraries.SeleniumLibrary.Selenium;

public class DataProviderTestClass extends Selenium{
	
	@DataProvider
	public static  Object[][] login()  {
		try {
			return getExcelData("TestFile.xlsx", "LoginTest");
		} catch (Exception e) {
			System.out.println("Some exception occure " + e.getMessage());
		}
		return null;
	}

	
}
