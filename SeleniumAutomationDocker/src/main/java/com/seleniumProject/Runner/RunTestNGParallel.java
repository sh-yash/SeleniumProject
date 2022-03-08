package com.seleniumProject.Runner;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.seleniumProject.Utils.Logging;

public class RunTestNGParallel implements Runnable {

	ArrayList<String> serverIP;
	LinkedHashMap<String, String> testcases;
	Thread t;
	Logging logger;
	int updatedServerSize = 0;

	RunTestNGParallel(LinkedHashMap<String, String> testcases, ArrayList<String> serverIP, 	Logging logger) {
		this.logger = logger;
		this.testcases = testcases;
		this.serverIP = serverIP;
		t = new Thread(this);
		t.start();
	}

	
	private String getUrlModule(String website) {
		if (website.equalsIgnoreCase("designroom")) {
			for (String str : serverIP)
				if (str.contains("dr"))
					return str.split(",")[1];
		} else if (website.equalsIgnoreCase("dataanalytics")) {
			for (String str : serverIP)
				if (str.contains("da,"))
					return str.split(",")[1];
		} else if (website.equalsIgnoreCase("dsp")) {
			for (String str : serverIP)
				if (str.contains("dsp,"))
					return str.split(",")[1];
		} else if (website.equalsIgnoreCase("corporate")) {
			for (String str : serverIP)
				if (str.contains("co,"))
					return str.split(",")[1];
		} else if (website.equalsIgnoreCase("video")) {
			for (String str : serverIP)
				if (str.contains("video,"))
					return str.split(",")[1];
		} else if (website.equalsIgnoreCase("sales")) {
			for (String str : serverIP)
				if (str.contains("sales"))
					return str.split(",")[1];
		} else {
			for (String str : serverIP)
				if (!str.contains(","))
					return str;
		}
		return null;
	}
	
	
	public void run() {

		try {
			TestNG myTestNG = new TestNG();
			myTestNG.setUseDefaultListeners(false);
			XmlSuite mySuite = new XmlSuite();
			Map<String, String> paramsyash = null;
			mySuite.setName("seleniumProject_Automation");
			mySuite.setParallel(XmlSuite.ParallelMode.TESTS);
			mySuite.setThreadCount(2);
			logger.logInfo("Adding testcases in testng.xml");
			for (Entry<String, String> entry : testcases.entrySet()) {
				String testcaseID = (String) entry.getKey();
				String testcaseDetails = entry.getValue();
				String module = testcaseDetails.split(",")[1];
				String website = testcaseDetails.split(",")[2];
				String browser= testcaseDetails.split(",")[3];
			    String[] platformList=testcaseDetails.split(",")[5].split(",");
				if (!(browser.equalsIgnoreCase("firefox")||browser.equalsIgnoreCase("Edge")||browser.equalsIgnoreCase("IE"))) {
					browser="chrome";
				}
				
				if (module.equalsIgnoreCase("docker")) {
					
					for (String os : platformList) {
						Map<String, String> params = new HashMap<String, String>();
						params.put("module", module);
						params.put("serverIP", getUrlModule(website));
						params.put("browser", browser);
						XmlTest myTest = new XmlTest(mySuite);
						myTest.setName("" + testcaseID + "_" + os);
						ArrayList<XmlClass> myClasses = new ArrayList<XmlClass>();
						if (os.toLowerCase().contains("windows")) {
							os = "windows";
						}
						switch (os.toLowerCase()) {
						case "windows":
							params.put("clienv", "windows");
							break;
						case "osx":
							params.put("clienv", "osx");
							break;
						case "portable":
							params.put("clienv", "portable");
							break;
						default:
							params.put("clienv", "osx");
							break;
						}
					

						// params.put("Listner", listnerClass);

						myTest.setParameters(params);
						myClasses.add(new XmlClass("com.seleniumProject.Tests.Docker.QA" + testcaseID + "TestRunner"));
						myTest.setXmlClasses(myClasses);
					}
				} else {
					Map<String, String> params = new HashMap<String, String>();
					params.put("module", module);
					params.put("serverIP", getUrlModule(website));
					params.put("browser", browser);
					params.put("clienv", Starter.OS);
					// params.put("Listner", listnerClass);

					XmlTest myTest = new XmlTest(mySuite);
					myTest.setName("" + testcaseID);
					myTest.setParameters(params);
					ArrayList<XmlClass> myClasses = new ArrayList<XmlClass>();

					if (website.equalsIgnoreCase("designroom")) {
						switch (module.toLowerCase()) {
						case "login":
							myClasses.add(
									new XmlClass("DesignRoom.Login." + testcaseID + "TestRunner"));
							break;
						case "sanity":
							myClasses.add(
									new XmlClass("DesignRoom.Sanity." + testcaseID + "TestRunner"));
							break;
						case "VTAutomation":
							myClasses.add(new XmlClass("DesignRoom.VTAutomation." + testcaseID + "TestRunner"));
							break;	
						}
						myTest.setXmlClasses(myClasses);
					} else if (website.equalsIgnoreCase("dataanalytics")) {
						switch (module.toLowerCase()) {
						case "login":
							myClasses.add(new XmlClass(
									"DataAnalytics.Login." + testcaseID + "TestRunner"));
							break;
						case "sanity":
							myClasses.add(new XmlClass(
									"DataAnalytics.Sanity." + testcaseID + "TestRunner"));
							break;
						case "VTAutomation":
							myClasses.add(new XmlClass("DataAnalytics.VTAutomation." + testcaseID + "TestRunner"));
							break;	
						}
						myTest.setXmlClasses(myClasses);
					} else if (website.equalsIgnoreCase("corporate")) {
						switch (module.toLowerCase()) {
						case "login":
							myClasses.add(
									new XmlClass("Corporate.Login." + testcaseID + "TestRunner"));
							break;
						case "sanity":
							myClasses.add(
									new XmlClass("Corporate.Sanity." + testcaseID + "TestRunner"));
							break;
						case "VTAutomation":
							myClasses.add(new XmlClass("Corporate.VTAutomation." + testcaseID + "TestRunner"));
							break;	

						}
						myTest.setXmlClasses(myClasses);

					} else if (website.equalsIgnoreCase("dsp")) {
						switch (module.toLowerCase()) {
						case "login":
							myClasses.add(new XmlClass("DSP.Login." + testcaseID + "TestRunner"));
							break;
						case "sanity":
							myClasses.add(new XmlClass("DSP.Sanity." + testcaseID + "TestRunner"));
							break;
						case "VTAutomation":
							myClasses.add(new XmlClass("DSP.VTAutomation." + testcaseID + "TestRunner"));
							break;	
						}
						myTest.setXmlClasses(myClasses);

					} else if (website.equalsIgnoreCase("video")) {
						switch (module.toLowerCase()) {
						case "video":
							myClasses.add(new XmlClass("Video" + testcaseID + "TestRunner"));
							break;
						}
						myTest.setXmlClasses(myClasses);

					}

				}
			}

			ArrayList<XmlSuite> mySuites = new ArrayList<XmlSuite>();
			mySuites.add(mySuite);
			myTestNG.setXmlSuites(mySuites);

			
			FileWriter writer;
			writer = new FileWriter(new File("myTempParallel.xml"));
			writer.write(mySuite.toXml());
			writer.flush();
			writer.close();

			logger.logInfo("Triggering execution");
	
			myTestNG.run();

		} catch (Exception e) {
			System.err.println(e.getMessage());
			logger.logError(e.getMessage());
		}

	}

}

