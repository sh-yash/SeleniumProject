
package com.seleniumProject.Runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.spotify.docker.client.messages.Container;
import com.seleniumProject.Libraries.CLILibrary.CLICommandExecutor;
import com.seleniumProject.Utils.Containers;
import com.seleniumProject.Utils.Logging;
import com.seleniumProject.Utils.OneTime;
import com.seleniumProject.Utils.TestInitialization;
import com.seleniumProject.Utils.UpdateCheck;


public class Controller {
	String executionType;
	String isParallel;

	TestInitialization testInitialization;
	Containers ob = new Containers();
	CLICommandExecutor cLICommandExecutor = new CLICommandExecutor();
	Logging logger;
	ArrayList<String> serverDetails;
	public static ArrayList<String> module = new ArrayList<String>();

	private static HashMap<String, Boolean> serverStatus = new HashMap<String, Boolean>();
	private static HashMap<String, List<String>> gridConfig = new HashMap<String,List<String>>();
	private static HashMap<String, Boolean> containerStatusForCLI = new HashMap<String, Boolean>();

	/**
	 * Constructor of Controller class.
	 * 
	 * @param executionType
	 * @param isParallel
	 * @param updatedServers
	 */
	public Controller(String executionType, String isParallel, ArrayList<String> serverDetails, Logging logger) {
		this.logger = logger;
		this.isParallel = isParallel;
		this.executionType = executionType;
		this.serverDetails = serverDetails;
		testInitialization = new TestInitialization(logger);
		logger.logInfo("Constructor of Controller class initialized.");
	}

	/**
	 * Created by Yash .. Based on the inputs available in
	 * executionDetails.properties file this function create TestNG.xml and
	 * trigger. the execution
	 * 
	 * @throws InterruptedException
	 */
	public void manageTestNGXML() throws InterruptedException {
		OneTime oneTime = new OneTime();
		oneTime.oneTimeSetup();
		LinkedHashMap<String, String> testcases = null;
		Set<String> hash_Set_Available_server = new HashSet<String>();
		logger.logInfo("switching to case: " + executionType.toLowerCase());
		switch (executionType.toLowerCase()) {

		case "selected":
			testcases = testInitialization.getTestcasesByID(testInitialization.getTestcasesFromProperties());
			logger.logInfo("Total test cases to be executed: " + testcases.size());
			if (testcases.size() == 0) {
				logger.logError("Please mark at-least one test case as 'Yes' in order to start execution.");
				System.exit(0);
			}
			if (Starter.dicConfig.get("isParallel").equalsIgnoreCase("no")
					|| Starter.dicConfig.get("isParallel").equalsIgnoreCase("n")) {
				if (TestInitialization.isPlatformOSX
						&& Starter.dicConfig.get("Platform").toLowerCase().contains("osx")) {

					Starter.dicConfig.put("isDocker", "Y");
				}
				if (Starter.dicConfig.get("isDocker").equalsIgnoreCase("y")
						|| Starter.dicConfig.get("isDocker").equalsIgnoreCase("yes")) {
					if (startDockerBasedContainers()) {
						new RunTestNGSequentially(testcases, serverDetails,  logger).t
								.join();
					} else {
						logger.logError("Unable to start container.");
					}
				} else if (Starter.dicConfig.get("isGrid").equalsIgnoreCase("y")
						|| Starter.dicConfig.get("isGrid").equalsIgnoreCase("yes")) {
					initializeGridSetup();
					new RunTestNGSequentially(testcases, serverDetails, logger).t
					.join();
				} else {
					new RunTestNGSequentially(testcases, serverDetails,  logger).t
							.join();
				}
			} else {
						new RunTestNGParallel(testcases, serverDetails , logger).t.join();
			}
			break;
		case "complete":
			testcases = testInitialization.getTestcases();
			logger.logInfo("Total test cases to be executed: " + testcases.size());
			if (testcases.size() == 0) {
				logger.logError("0 testcase return, please check Testsuite.xml");
				System.exit(0);
			}
			if (Starter.dicConfig.get("isParallel").equalsIgnoreCase("no")
					|| Starter.dicConfig.get("isParallel").equalsIgnoreCase("n")) {
				if (TestInitialization.isPlatformOSX
						&& Starter.dicConfig.get("Platform").toLowerCase().contains("osx")) {

					Starter.dicConfig.put("isDocker", "Y");
				}
				if (Starter.dicConfig.get("isDocker").equalsIgnoreCase("y")
						|| Starter.dicConfig.get("isDocker").equalsIgnoreCase("yes")) {
					if (startDockerBasedContainers()) {
						new RunTestNGSequentially(testcases, serverDetails,  logger).t
								.join();
					} else {

						logger.logError("Unable to start container.");
					}
				} else {
					new RunTestNGSequentially(testcases, serverDetails,  logger).t
							.join();
				}
			} else {
				if (serverDetails.size() == 1) {
					System.out.println(
							"Please provide more than 1 servers in order to execute scripts in parallel. Terminating execution process.");
					System.exit(0);
				} else {
					if (initializeGridSetup()) {
						Starter.dicConfig.put("isDocker", "Y");
						// Parallel code
						// Needs to be tested and implemented
					}
				}
			}
			break;
		case "component":
			testcases = getTestCasesOfSelectedComponent();
			logger.logInfo("Total test cases to be executed: " + testcases.size());
			if (testcases.size() == 0) {
				logger.logError("0 testcase return, please check Testsuite.xml");
				System.exit(0);
			}
			if (Starter.dicConfig.get("isParallel").equalsIgnoreCase("no")
					|| Starter.dicConfig.get("isParallel").equalsIgnoreCase("n")) {
				if (TestInitialization.isPlatformOSX
						&& Starter.dicConfig.get("Platform").toLowerCase().contains("osx")) {
					Starter.dicConfig.put("isDocker", "Y");
				}
				if (Starter.dicConfig.get("isDocker").equalsIgnoreCase("y")
						|| Starter.dicConfig.get("isDocker").equalsIgnoreCase("yes")) {
					if (startDockerBasedContainers()) {
						new RunTestNGSequentially(testcases, serverDetails, logger).t
								.join();
					} else {
						logger.logError("Unable to start container.");
					}
				} else {
					new RunTestNGSequentially(testcases, serverDetails, logger).t
							.join();
				}
			} else {
				if (serverDetails.size() == 1) {
					System.out.println(
							"Please provide more than 1 servers in order to execute scripts in parallel. Terminating execution process.");
					System.exit(0);
				} else {
					if (initializeGridSetup()) {
						Starter.dicConfig.put("isDocker", "Y");
						// Parallel code
						// Needs to be tested and implemented
					}
				}
			}
			break;
		default:
			break;
		}

		try {
		//	oneTime.stopVideoRecorder();
		} catch (Exception e) {
			System.out.println("Recording feature is in stop condition");
		}

		
		
		try {
			OneTime.oneTimeTearDown();
		} catch (Exception e) {
			System.out.println("Something went wrong " +e.getMessage());
		}
		
		if (Starter.dicConfig.get("isDocker").equalsIgnoreCase("y")
				|| Starter.dicConfig.get("isDocker").equalsIgnoreCase("yes")) {
			ob.stopContainers();
		}
	}

	/**
	 * Created by Yash .. It will fetch the test caes from Testsuite.xml based
	 * on the components marked as Yes in components.properties file.
	 * 
	 * @return
	 */
	private LinkedHashMap<String, String> getTestCasesOfSelectedComponent() {
		LinkedHashMap<String, String> testcases = new LinkedHashMap<String, String>();
		try {
			testcases = testInitialization.getTestcasesByComponent(getComponentsAndModule());
		} catch (Exception e) {
			testcases = null;
			e.printStackTrace();
		}
		return testcases;
	}

	/**
	 * Created by Yash .. It will fetch the components from
	 * components.properties file which are marked as Yes or Y
	 * 
	 * @return
	 */
	private ArrayList<String> getComponents() {
		ArrayList<String> components = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("PropertiesFile/components.properties");
			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				System.out.println("Key : " + key + ", Value : " + value);
				if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
					components.add(key);
				}
			}
		} catch (Exception e) {
			components = null;
		}

		return components;
	}

	private ArrayList<String> getComponentsAndModule() {

		ArrayList<String> components = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("PropertiesFile/components.properties");
			prop.load(input);

			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				System.out.println("Key : " + key + ", Value : " + value);
				if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {

					if (key.equalsIgnoreCase("designroom") || key.equalsIgnoreCase("da") || key.equalsIgnoreCase("dsp")
							|| key.equalsIgnoreCase("corporate")) {
						module.add(key);
					} else {
						components.add(key);
					}
				}
			}
		} catch (Exception e) {
			components = null;
		}
		return components;
	}

	/**
	 * Created by Yash .. It will start the Chrome/Firefox based container to
	 * execute the test cases sequentially.
	 * 
	 * @return
	 */
//	public boolean startDockerBasedBrowser() {
//		boolean flag = false;
//
//		switch (Starter.dicConfig.get("browser").toLowerCase()) {
//		case "chrome":
//			flag = ob.startContainers(Starter.dicConfig.get("chromeDockerImage"), ob,
//					"/Docker/Selenium_Standalone_Chrome");
//			if (flag) {
//				flag = ob.isContainerRunning(Starter.dicConfig.get("chromeDockerImage"));
//			}
//			break;
//		case "firefox":
//			flag = ob.startContainers(Starter.dicConfig.get("firefoxDockerImage"), ob,
//					"/Docker/Selenium_Standalone_Firefox");
//			if (flag) {
//				flag = ob.isContainerRunning(Starter.dicConfig.get("firefoxDockerImage"));
//			}
//			break;
//		}
//		if (flag) {
//			final List<Container> containers = ob.getRunningContainers();
//			for (Container im : containers) {
//				containerStatusForCLI.put(im.id(), false);
//			}
//		}
//		return flag;
//	}
	
	
	public boolean startDockerBasedContainers(){
		boolean flag=false;
		
		Containers cont=new Containers();
		if(cont.startDockerMachine()){
			
		if(cont.writeDockerComposeFile(cont))
		{
			try {
				if(cont.triggerComposeFile()){
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		}
		else{
			flag=false;
		}
		return flag;
		
		
		
	}

	/**
	 * Created by Yash .. It will write the docker-compose.yml file based on the
	 * number of nodes required and initiate all the hub and node containers
	 * 
	 * @return
	 */
	/*
	 * public boolean initializeGridSetUp() { boolean flag = true; //if
	 * (ob.writeDockerComposeFile(updatedServers.size(), ob)) { try { Process p
	 * = Runtime.getRuntime().exec("/usr/local/bin/docker-compose up -d");
	 * StringWriter swr = new StringWriter();
	 * IOUtils.writeLines(IOUtils.readLines(p.getErrorStream(), "utf-8"),
	 * IOUtils.LINE_SEPARATOR_WINDOWS, swr); if
	 * (swr.toString().contains("deviceconnectautomation_seleniumhub_1")) { flag
	 * = ob.isContainerRunning("selenium/hub"); } if (flag) { for (String server
	 * : serverDetails) { serverStatus.put(server, false); } flag = true; } if
	 * (flag) { final List<Container> containers = ob.getRunningContainers();
	 * for (Container im : containers) { containerStatusForCLI.put(im.id(),
	 * false); } } } catch (Exception e) { flag = false; e.printStackTrace(); //
	 * } } return flag;}
	 */
	public boolean initializeGridSetup() {
		try {
			String standAlonePath = new File(".").getCanonicalPath()
					+ "\\Artifacts\\InFiles\\selenium-server-standalone-3.141.59.jar";
			String command = "java -jar " + standAlonePath + " -role hub";
			cLICommandExecutor.executeCLICommand(command,Starter.dicConfig.get("SystemUserName"), Starter.dicConfig.get("SystemUserPassword"), System.getProperty("os.name"));
			Thread.sleep(4000);
			
			if(!loadGridDetails())
				logger.logError("Error in loading the grid configuration for run");
			else
				logger.logInfo("Grid Details have loaded successfully");
			
			configureNode();
			
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public boolean configureNode(){
		UpdateCheck ssh= new UpdateCheck();
		int noOfNode=gridConfig.get("Node_IP").size();
		String host=null;
		String userName=null;
		String password=null;
		String browserDriverPath=null;
		String standAlonePath=null;
		String command=null;
	
		try{
		for (int i = 0; i < noOfNode; i++) {
		for(Map.Entry<String,List<String>> entry: gridConfig.entrySet())
		{
		    if(entry.getKey().equalsIgnoreCase("Node_IP"))
		    {
		    	host=entry.getValue().get(i);
		    }else if(entry.getKey().equalsIgnoreCase("Node_UserName"))
		    {
		    	userName=entry.getValue().get(i);
		    }else if(entry.getKey().equalsIgnoreCase("Node_Password"))
		    {
		    	password=entry.getValue().get(i);
		    }else if(entry.getKey().equalsIgnoreCase("Node_PathDriver"))
		    {
		    	browserDriverPath=entry.getValue().get(i);
		    }else if(entry.getKey().equalsIgnoreCase("Node_PathSeleniumStandAlone"))
		    {
		    	standAlonePath=entry.getValue().get(i);
		    }
		}
		 command="java -Dwebdriver.chrome.driver=" +browserDriverPath+ " -jar "+ standAlonePath +" -role node -hub http://192.168.254.86:4444/grid/register";
		 ssh.executeSSHCommand(host, userName, password, command);
		}
		return true;
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	
	
	public boolean loadGridDetails(){
	boolean flag = false;
	ArrayList<String> nodeName= new ArrayList<String>();
	Properties prop = new Properties();
	InputStream input = null;
	logger.logInfo("Loading  Grid configuration in 'gridConf' from gridDetails.properties");
	try {
		input = new FileInputStream("PropertiesFile/gridDetails.properties");
		prop.load(input);
		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = prop.getProperty(key);
			List<String> val=Arrays.asList(value.split(","));
			gridConfig.put(key, val);
			}
     
		if (!(gridConfig.isEmpty())) {
			flag = true;
		}

	} catch (Exception e) {
		flag = false;
		logger.logError(e.getMessage());
	}
	logger.logInfo("Configuration Grid loaded - " + flag);
	return flag;
     }
	
	/**
	 * Created by Yash .. It will return the available container on which no
	 * bash command running.
	 * 
	 * @return
	 */
	public static synchronized String getContainersInstance() {
		String containerID = "";
		for (Entry<String, Boolean> entry : containerStatusForCLI.entrySet()) {
			if (!(Boolean) entry.getValue()) {
				containerID = entry.getKey();
				entry.setValue(true);
				break;
			}
		}
		return containerID;
	}

	/**
	 * Created by Yash .. It will set the containerInstance state as false.
	 * where false indicate conatinerInstance is available for execution.
	 * 
	 * @param key
	 */
	public static synchronized void setContainerStatus(String key) {
		containerStatusForCLI.put(key, false);
	}

	/**
	 * Created by Yash .. It will return the available server on which script
	 * can be executed. Used for managing the server availability among the
	 * nodes.
	 * 
	 * @return
	 */
	public static synchronized String getServerURL() {
		String url = "";
		for (Entry<String, Boolean> entry : serverStatus.entrySet()) {
			if (!(Boolean) entry.getValue()) {
				url = entry.getKey();
				entry.setValue(true);
				break;
			}
		}
		return url;
		
	}

	/**
	 * Created by Yash .. It will set the server state as true/false. where
	 * false indicate server is available for execution.
	 * 
	 * @param key
	 * @param value
	 */
	public static synchronized void setServerStatus(String key, Boolean value) {
		serverStatus.put(key, value);
		// System.out.println(serverStatus.toString());
	}

}
