package com.seleniumProject.Runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import com.seleniumProject.Utils.DeleteLogsAndReports;
import com.seleniumProject.Utils.Logging;
import com.seleniumProject.Utils.UpdateCheck;

/**
 * 
 * 
 * @author Yash pre req : server should have automation_tester@gmail.com user
 *         with Tester Role
 */
public class Starter {

	public static String OS = System.getProperty("os.name");
	public static HashMap<String, String> dicConfig = new HashMap<String, String>();
	private static Logging logger = new Logging();

	/**
	 * It is the entry point of Framework. It loads the configuration files,
	 * check server state and start accordingly.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		try {
			DeleteLogsAndReports delete = new DeleteLogsAndReports();
			delete.deleteFiles("logs");
			delete.deleteFiles("Reports");
		
			ArrayList<String> serverDetails = new ArrayList<String>();
			Starter obj = new Starter();

			obj.deleteExistingOUTFiles();
			UpdateCheck updateCheck = new UpdateCheck();

			if (!obj.loadConfigurations()) {
				logger.logError(
						"Unable to read executionDetails.properties file. Terminating execution.Check the stack.");
				System.exit(0);
			}

			logger.logInfo("Getting updated url from properties file");
			serverDetails = updateCheck.getServerIPs();

			// Check, whether any server available.
			if (serverDetails.isEmpty()) {
				logger.logError("Server list is empty. Please check manually.");
				System.exit(0);
			}

			Controller controller = new Controller(dicConfig.get("executionType"), dicConfig.get("isParallel"),
					serverDetails, logger);

			controller.manageTestNGXML();

		} catch (Exception e) {
			logger.logError("Something went wrong in Starter file" +e.getMessage());
			}
	}

	/**
	 * Created by Yash .. Function to load executionDetails.properties file in
	 * dicConfig.
	 * 
	 * @return
	 */
	public boolean loadConfigurations() {
		boolean flag = false;
		Properties prop = new Properties();
		InputStream input = null;
		logger.logInfo("Loading configuration in 'dicConfig' from executionDetails.properties");
		try {
			input = new FileInputStream("PropertiesFile/executionDetails.properties");
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				dicConfig.put(key, value);
			}
			if (!(dicConfig.isEmpty())) {
				flag = true;
			}

		} catch (Exception e) {
			flag = false;
			logger.logError(e.getMessage());
		}
		logger.logInfo("Configuration loaded - " + flag);
		return flag;
	}

	/**
	 * Created by Yash .. This function will delete all '/Artifacts/OUTFiles'.
	 * 
	 * @return
	 */
	public boolean deleteExistingOUTFiles() {
		boolean flag = false;
		try {
			logger.logInfo("Delete /Artifacts/OUTfiles.");
			ArrayList<File> listOfFiles = new ArrayList<>();
			listOfFiles = DeleteLogsAndReports
					.getListOfFiles(new File(new File(".").getCanonicalPath() + "/Artifacts/OUTFiles/"));
			for (File file : listOfFiles) {
				if (file.isDirectory()) {
					DeleteLogsAndReports.deleteNestedFiles(file);
				} else {
					file.delete();
				}
			}
			flag = true;
			logger.logInfo("All OUTFiles deleted successfully.");
		} catch (Exception e) {
			flag = false;
			logger.logError(e.getMessage());
		}
		return flag;
	}
	public static void testLogicImple() {
	 
		try {
			main(null);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
