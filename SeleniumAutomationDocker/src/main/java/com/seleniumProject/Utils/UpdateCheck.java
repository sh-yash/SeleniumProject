package com.seleniumProject.Utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class UpdateCheck {
	public String buildFromDropbox;
	public String serverIP;
	public String user;
	public String passcode;
	public Logging logger = new Logging();

	/**
	 * Created by Yash .. It will fetch the details of server and check whether
	 * update required on that or not.
	 * 
	 * @param token
	 * @return
	 */
	public ArrayList<String> verifyBuildUpdated(String token) {
		ArrayList<String> updatedServer = new ArrayList<String>();

		LinkedHashMap<String, String> serverdetails = getServerDetails();
		buildFromDropbox = fetchBuildNoFromDropbox(token);
		for (Entry<String, String> entry : serverdetails.entrySet()) {
			String buildNo = (String) entry.getValue();
			if (buildNo.contains(buildFromDropbox)) {
				updatedServer.add(entry.getKey());
			}
		}
		logger.logInfo("Return updated server: " + updatedServer);
		return updatedServer;
	}

	/**
	 * Created by Yash .. It will fetch the serverIP from serverList.properties
	 * file which are marked as Yes/Y.
	 * 
	 * @return
	 */
	public ArrayList<String> getServerIPs() {
		ArrayList<String> serversIP = new ArrayList<String>();
		Properties prop = new Properties();
		InputStream input = null;
		logger.logInfo("Fetching servers from serverDetails.properties file based on Y/Yes.");
		try {
			input = new FileInputStream("PropertiesFile/serverDetails.properties");
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			System.out.println();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String[] value = prop.getProperty(key).split(",");
				if ((value[0].equalsIgnoreCase("y")) || (value[0].equalsIgnoreCase("yes"))) {

					if (value[1].equalsIgnoreCase("da") || value[1].equalsIgnoreCase("dr")
							|| value[1].equalsIgnoreCase("dsp") || value[1].equalsIgnoreCase("co")) {
						serversIP.add(value[1] + "," + "https://qadr.seleniumProject.com:" + key);
					} else if (value[1].equalsIgnoreCase("video")) {
						serversIP.add(value[1] + "," + "https://p.vdometer.com/preprod/" + key);
					} else if (value[1].equalsIgnoreCase("sales")) {
						serversIP.add(value[1] + "," + "https://demo.vtap.me/");
					} else {
						serversIP.add(key);
					}
				} else {
					continue;
				}

			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		logger.logInfo("Got server list - " + serversIP);
		return serversIP;
	}

	/**
	 * Created by Yash .. It will fetch the credentials of servers from
	 * serverDetails.propoerties file.
	 * 
	 * @param serverIPs
	 * @return
	 */
	public LinkedHashMap<String, String> getServerCredentials() {
		LinkedHashMap<String, String> serversCredentials = new LinkedHashMap<>();
		Properties prop = new Properties();
		InputStream input = null;
		logger.logInfo("Loading server credentials from serverDetails.properties file.");
		try {
			input = new FileInputStream("PropertiesFile/serverDetails.properties");
			prop.load(input);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String[] value = prop.getProperty(key).split(",");
				if ((value[0].equalsIgnoreCase("y")) || (value[0].equalsIgnoreCase("yes"))) {
					try {
						serversCredentials.put(key, value[2] + "," + value[3]);
					} catch (ArrayIndexOutOfBoundsException e1) {
						serversCredentials.put("null", "null");
					}
				}
			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
		}
		logger.logInfo("Fetched successfully credentials of servers - " + serversCredentials);
		return serversCredentials;
	}

	/**
	 * Created by Yash .. It will return the execution result of given command
	 * from the server.
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param command
	 * @return
	 */
	public String executeSSHCommand(String host, String user, String password, String command) {
		StringBuilder builder = new StringBuilder();
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			logger.logInfo("Server connected: " + host);
			System.out.println(command);
			Channel channel = session.openChannel("exec");
			logger.logInfo("Executing command: " + command);
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			 InputStream in = channel.getInputStream();
			 channel.connect();
			 Thread.sleep(5000);
			  @SuppressWarnings("rawtypes") List ls = IOUtils.readLines(in,"utf-8"); 
			  StringWriter swr = new StringWriter();
			  IOUtils.writeLines(ls, IOUtils.LINE_SEPARATOR_WINDOWS, swr);
			  builder.append(swr.toString()); channel.disconnect();
			  session.disconnect();
			  logger.logInfo("Any output from command execution: "+builder.
			  toString()); 
			  return builder.toString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.logError(e.getMessage());
			logger.logInfo("returnning output: " + builder.toString());
			return builder.toString();
		}
	}

	/**
	 * Created by Yash .. It will fetch the deviceConnect buildNo from the
	 * server.
	 * 
	 * @param IP
	 * @param user
	 * @param password
	 * @return
	 */

	public String getBuildNo(String IP, String user, String password) {
		String buildNo = "";
		buildNo = executeSSHCommand(IP, user, password, "curl -sS -L \"http://" + IP
				+ "\" | egrep '[0-9]\\.[0-9]\\.[0-9][0-9][0-9][0-9]\\.[0-9][0-9][0-9][0-9]' | tr -d ' '");

		if (buildNo.equals("")) {
			buildNo = executeSSHCommand(IP, user, password, "curl -k -sS -L \"https://" + IP
					+ "\" | egrep '[0-9]\\.[0-9]\\.[0-9][0-9][0-9][0-9]\\.[0-9][0-9][0-9][0-9]' | tr -d ' '");
		}
		System.out.println(buildNo);
		return buildNo;
	}

	/**
	 * Created by Yash .. It will fetch the latest master build number from
	 * dropbox.
	 * 
	 * @param token
	 * @return
	 */
	public String fetchBuildNoFromDropbox(String token) {
		String dropboxBuild = "";
		try {
			logger.logInfo("Fetch latest master build no from dropbox.");
			@SuppressWarnings("deprecation")
			DbxRequestConfig config = new DbxRequestConfig("dropbox/Mobile Labs", "en_US");
			DbxClientV2 client = new DbxClientV2(config, token);
			FullAccount account = client.users().getCurrentAccount();
			System.out.println(account.getName().getDisplayName());
			ListFolderResult result = client.files().listFolder("/Distribution");
			while (true) {
				for (Metadata metadata : result.getEntries()) {
					if (metadata.getPathLower().contains("master")) {
						dropboxBuild = metadata.getPathLower();
					}
				}
				if (!result.getHasMore()) {
					break;
				}
				result = client.files().listFolderContinue(result.getCursor());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.logError(e.getMessage());
		}
		if (dropboxBuild.length() > 0) {
			dropboxBuild = dropboxBuild.split("/")[2];
		}
		logger.logInfo("Latest master build on dropbox:  " + dropboxBuild);
		return dropboxBuild;
	}

	/**
	 * Created by Yash .. It will fetch the server details. like deviceConnect
	 * version, serverIP, etc,.
	 * 
	 * @return
	 */
	public LinkedHashMap<String, String> getServerDetails() {
		LinkedHashMap<String, String> serverdetails = new LinkedHashMap<>();
		LinkedHashMap<String, String> serverCredentials = getServerCredentials();
		logger.logInfo("Fetch user credentials.");
		for (Entry<String, String> entry : serverCredentials.entrySet()) {
			String key = entry.getKey();
			String[] value = entry.getValue().split(",");
			serverdetails.put(key, getBuildNo(key, value[0], value[1]));
			serverIP = (String) entry.getKey();
			user = value[0];
			passcode = value[1];
		}
		logger.logInfo("Credentials fetched - " + serverdetails);
		return serverdetails;
	}

	/**
	 * Created by Yash .. It will copy the specified file
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param command
	 * @return
	 */
	public void copyFile(String host, String user, String password, String filePath, String targetPath) {
		Session session = null;
		Channel channel = null;
		try {
			JSch ssh = new JSch();
			JSch.setConfig("StrictHostKeyChecking", "no");
			session = ssh.getSession(user, host, 22);
			session.setPassword(password);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp) channel;
			sftp.put(filePath, targetPath);
		} catch (Exception e) {
			logger.logError("UpdateCheck --> copyFile() : " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

	}

	/**
	 * Created by Yash .. It will return login credentials of server.
	 * 
	 * @param IP
	 * @return
	 */
	public ArrayList<String> getServerCredentialsOfServer(String IP) {
		ArrayList<String> creds = new ArrayList<String>();
		try {
			for (Entry<String, String> entry : getServerCredentials().entrySet()) {
				if (entry.getKey().equals(IP)) {
					String[] value = entry.getValue().split(",");
					creds.clear();
					creds.add(IP);
					creds.add(value[0]);
					creds.add(value[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return creds;
	}
}
