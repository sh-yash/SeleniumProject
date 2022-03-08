package com.seleniumProject.Libraries.ApplicationLibrary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.seleniumProject.Utils.Logging;
import com.seleniumProject.Utils.UpdateCheck;

public class AppLib {
	private static Logging logger = new Logging();

	/**
	 * It will return build no of Server.
	 * 
	 * @param IP
	 * @return
	 */
	public String getBuildNoOfServer(String IP) {
		String buildNo = "";
		try {
			UpdateCheck updateCheck = new UpdateCheck();
			LinkedHashMap<String, String> serverCredentials = updateCheck.getServerCredentials();
			for (Entry<String, String> entry : serverCredentials.entrySet()) {
				if (entry.getKey().equals(IP)) {
					String[] value = entry.getValue().split(",");
					buildNo = updateCheck.getBuildNo(IP, value[0], value[1]);
					break;
				}
			}
			logger.logInfo("build available on server: " + IP + " is: " + buildNo);
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		return buildNo;
	}


}
