package com.seleniumProject.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.seleniumProject.Runner.Controller;

public class TestInitialization {

	Logging logger;
	public static boolean isPlatformOSX;

	public TestInitialization(Logging logger) {
		this.logger = logger;
	}

	/**
	 * Created by Yash .. It will fetch the testcases details from Testsuite.xml
	 * based on the testscript's id provided.
	 * 
	 * @param components
	 * @return
	 */
	public LinkedHashMap<String, String> getTestcasesByID(ArrayList<String> testcases) {
		LinkedHashMap<String, String> testCasesInfo = new LinkedHashMap<>();
		String testScriptID = "";
		String testCaseDetails = "";
		logger.logInfo("Fetching complete information of testcases from Testsuite.xml.");
		try {
			File fXmlFile = new File("Testsuite.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("testcase");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (testcases.contains(eElement.getElementsByTagName("TestCaseId").item(0).getTextContent())) {
						testScriptID = eElement.getElementsByTagName("TestCaseId").item(0).getTextContent();
						testCaseDetails = (eElement.getElementsByTagName("TestCaseName").item(0).getTextContent())
								.toString() + ","
								+ (eElement.getElementsByTagName("Module").item(0).getTextContent()).toString()  +","
								+ (eElement.getElementsByTagName("Website").item(0).getTextContent().toString()) +","
								+ (eElement.getElementsByTagName("Browser").item(0).getTextContent().toString()) +","
								+ (eElement.getElementsByTagName("JIRA_Id").item(0).getTextContent()).toString() +","
								+ (eElement.getElementsByTagName("Platform").item(0).getTextContent().toString());
						if ((eElement.getElementsByTagName("Platform").item(0).getTextContent()).toString()
								.equalsIgnoreCase("osx")) {
							isPlatformOSX = true;
						}
						testCasesInfo.put(testScriptID, testCaseDetails);
					}

				}
			}
		} catch (Exception e) {
			logger.logInfo(e.getMessage());
		}
		return testCasesInfo;
	}

	/**
	 * Created by Yash .. It will fetch the testcases from testcases.properties
	 * file which are marked as Yes/Y.
	 * 
	 * @return
	 */
	public ArrayList<String> getTestcasesFromProperties() {
		ArrayList<String> testCasesInfo = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		logger.logInfo("Fetching testcases marked as Y/Yes from testcases.properties file.");
		try {
			input = new FileInputStream("PropertiesFile/testcases.properties");
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				System.out.println("Key : " + key + ", Value : " + value);
				if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
					testCasesInfo.add(key);
				}
			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		return testCasesInfo;
	}

	/**
	 * Created by Yash .. It will fetch all the testcases details from
	 * Testsuite.xml
	 * 
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getTestcases() {
		LinkedHashMap<String, String> testCasesInfo = new LinkedHashMap<>();
		String testScriptID = "";
		String testCaseDetails = "";
		logger.logInfo("Fetching all testcases from Testsuite.xml.");
		try {
			File fXmlFile = new File("Testsuite.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("testcase");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					testScriptID = eElement.getElementsByTagName("TestCaseId").item(0).getTextContent();
					testCaseDetails = (eElement.getElementsByTagName("TestCaseName").item(0).getTextContent())
							.toString() + ","
							+ (eElement.getElementsByTagName("Module").item(0).getTextContent()).toString() + ","
							+ (eElement.getElementsByTagName("Website").item(0).getTextContent().toString()) + ","
							+ (eElement.getElementsByTagName("JIRA_Id").item(0).getTextContent()).toString();
					if ((eElement.getElementsByTagName("Platform").item(0).getTextContent()).toString()
							.equalsIgnoreCase("osx")) {
						isPlatformOSX = true;
					}
					testCasesInfo.put(testScriptID, testCaseDetails);
				}
			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		return testCasesInfo;
	}

	/**
	 * Created by Yash .. It will fetch the testcases details from Testsuite.xml
	 * based on the component name provided.
	 * 
	 * @param components
	 * @return
	 */
	public LinkedHashMap<String, String> getTestcasesByComponent(ArrayList<String> components) {
		LinkedHashMap<String, String> testCasesInfo = new LinkedHashMap<>();
		String testScriptID = "";
		String testCaseDetails = "";
		logger.logInfo("Fetching complete information of testcases from Testsuite.xml.");
		try {
			File fXmlFile = new File("Testsuite.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("testcase");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (Controller.module
							.contains(eElement.getElementsByTagName("Website").item(0).getTextContent().toString())) {
						if (components.contains(
								eElement.getElementsByTagName("Module").item(0).getTextContent().toString())) {
							testScriptID = eElement.getElementsByTagName("TestCaseId").item(0).getTextContent();
							testCaseDetails = (eElement.getElementsByTagName("TestCaseName").item(0).getTextContent())
									.toString() + ","
									+ (eElement.getElementsByTagName("Module").item(0).getTextContent()).toString()
									+ ","
									+ (eElement.getElementsByTagName("Website").item(0).getTextContent().toString())
									+ ","
									+ (eElement.getElementsByTagName("JIRA_Id").item(0).getTextContent()).toString();
							if ((eElement.getElementsByTagName("Platform").item(0).getTextContent()).toString()
									.equalsIgnoreCase("osx")) {
								isPlatformOSX = true;
							}

						}
						testCasesInfo.put(testScriptID, testCaseDetails);
					}

				}
			}
		} catch (Exception e) {
			logger.logInfo(e.getMessage());
		}
		return testCasesInfo;
	}
}
