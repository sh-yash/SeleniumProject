package com.seleniumProject.Utils;

/*import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;
*/
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.seleniumProject.Libraries.SeleniumLibrary.Selenium;
import com.seleniumProject.Runner.Starter;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;

/**
 * 
 * @author Yash .
 *
 */
public class OneTime {
	public ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	private String currentDate;
	public static String reportDirectory;
	public static DateFormat dateFormat;
	public static Date date;
	public static ExtentTest test;
	private static ATUTestRecorder recorder;

	/**
	 * This will generate the extent report
	 */
	public void oneTimeSetup() {
		try {
			dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			date = new Date();
			currentDate = dateFormat.format(date).toString();
			reportDirectory = "Reports/Run_" + currentDate;
			new File("Reports/Run_" + currentDate).mkdir();
			htmlReporter = new ExtentHtmlReporter(reportDirectory + "/seleniumProjectAutomation" + currentDate + ".html");
			htmlReporter.config().setDocumentTitle("seleniumProject-Automation-1.0");
			htmlReporter.config().setReportName("seleniumProject Automation 1.0");
						
			extent = new ExtentReports();
			extent.setSystemInfo("OS", "Window 10");
			extent.setSystemInfo("Host Name", "QA");
			extent.setSystemInfo("Environment", "QA Environment");
			extent.setSystemInfo("Designation", "QA Automation Engineer");
			extent.setSystemInfo("User Name", "QA Team");
			extent.setSystemInfo("Organisation", "seleniumProject Digital Pvt. Ltd.");

			extent.attachReporter(htmlReporter);
			
			if(Starter.dicConfig.get("isRecordingEnable").equalsIgnoreCase("y")||Starter.dicConfig.get("isRecordingEnable").equalsIgnoreCase("yes"))
			{
				initiateVideoRecorder();
			}
		} catch (Exception e) {
			System.out.println("Something went wrong during extent report creation or recording" + e.getMessage());
			
		}
	}
	
	 

	/**
	 * This will create the extent report
	 */
	public static void fail(Selenium driverBaseClass, String description, String expectedResult, String actualResult) {
		try {
			
		String className=Thread.currentThread().getStackTrace()[2].getClassName();	
		OneTime.test.fail(description, expectedResult, actualResult,
					MediaEntityBuilder
							.createScreenCaptureFromPath(driverBaseClass.saveScreenShot(driverBaseClass.driver,
									(Calendar.getInstance().getTime().toString().replace(" ", "_")).replace(":","-") +"-"+className+ ".png",
									OneTime.reportDirectory))
							.build(),
					new Throwable(actualResult));
		} catch (IOException e) {
			Logging.log.error(e.getMessage());
			
		}
	}

	/**
	 * This will sent the email report
	 */
	private static void sendEmailReport() {
		SendMailSSLWithAttachment sslWithAttachment = new SendMailSSLWithAttachment();
		sslWithAttachment.sendTestReportEmail();

	}

     
	/**
	 * This will tear down the test cases
	 */
	public static void oneTimeTearDown() {
		extent.flush();
		
		if(Starter.dicConfig.get("isRecordingEnable").equalsIgnoreCase("yes")||Starter.dicConfig.get("isRecordingEnable").equalsIgnoreCase("y"))
		{
		stopVideoRecorder();	
		}	
			
		if (Starter.dicConfig.get("isEmailEnable").equalsIgnoreCase("yes")|| Starter.dicConfig.get("isEmailEnable").equalsIgnoreCase("y")) {
			sendEmailReport();
		}

	}

	/**
	 * This will Start the video recording
	 */
	
	 private static void initiateVideoRecorder() { 
	 
	  try { 
		  String path = new File(".").getCanonicalPath()+"/"+reportDirectory+"/";
	   recorder = new ATUTestRecorder(path, "Video_Script_Test" + "Test", false);
	   recorder.start();
	  } 
	  catch (Exception e) {
	  System.out.println("Sorry Recording Feature is not working" +
	  e.getMessage()); 
	      }
	  }
	  
       private static void stopVideoRecorder() { 
       try {
    		 recorder.stop();
    	   } 
    	   catch(ATUTestRecorderException e) {
	      System.out.println("Sorry recording intrupt" + e.getMessage());
		       }
    	}
		 


}
