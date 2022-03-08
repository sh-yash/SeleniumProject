package com.seleniumProject.Libraries.CLILibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.seleniumProject.Runner.Controller;
import com.seleniumProject.Runner.Starter;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.ExecCreation;
import com.seleniumProject.Utils.Containers;
import com.seleniumProject.Utils.Logging;

public class CLICommandExecutor {
	private static Logging logger = new Logging();
	String currentDateAndTime = "";

	/**
	 * Created by Yash .. It will execute CLI command
	 * 
	 * @param command
	 * @param user
	 * @param password
	 */
	public String executeCLICommand(String command, String user, String password, String platform) {
		StringBuilder builder = new StringBuilder();
		try {
			Runtime runtime = Runtime.getRuntime();
			switch (platform.toLowerCase()) {

			case "portable":
				String[] permissionCommand = { "sh", "-c",
						"echo " + Starter.dicConfig.get("SystemUserPassword").toString() + " | sudo -S chmod 777 "
								+ new File(".").getCanonicalPath() + "/Artifacts/CLI/Portable/*" };
				runtime.exec(permissionCommand);
				Containers ob = new Containers();
				String id = Controller.getContainersInstance();
				System.out.println();
				try {
					final String[] fcommand = { "sh", "-c", command };
					final ExecCreation execCreation = ob.docker.execCreate(id, fcommand,
							DockerClient.ExecCreateParam.attachStdout(), DockerClient.ExecCreateParam.attachStderr());
					final LogStream output = ob.docker.execStart(execCreation.id());
					builder.append(output.readFully());
					logger.logInfo("Output of CLI command executed: " + builder.toString());
				} catch (Exception e) {
					logger.logError("Portable CLI execution, error: " + e.getMessage());
				} finally {
					Controller.setContainerStatus(id);
				}
				break;

			case "osx":
			case "windows 10":
				// Runtime runtime = Runtime.getRuntime();

				String[] finalCommand;
				if (platform.equalsIgnoreCase("osx")) {
					String[] permissionCommandTwo = { "sh", "-c",
							"echo " + Starter.dicConfig.get("SystemUserPassword").toString() + " | sudo -S chmod 777 "
									+ new File(".").getCanonicalPath() + "/Artifacts/CLI/OSX/*" };
					runtime.exec(permissionCommandTwo);
					Thread.sleep(2000);
					String[] fcommand = { "sh", "-c", command };
					finalCommand = fcommand;
				} else {
					String[] fcommand = { "cmd", "/c", command };
					finalCommand = fcommand;
				}
				try {
				
				ProcessBuilder pb = new ProcessBuilder().command(finalCommand);
			        pb.redirectErrorStream(true);
					Process p = pb.start();
					p.getInputStream().available();
					String output = IOUtils.toString(p.getInputStream());
					System.out.println(output);
					builder.append(output);

				/*
					 * BufferedReader reader = new BufferedReader(new
					 * InputStreamReader(p.getInputStream())); String line =
					 * null; while ( (line = reader.readLine()) != null) {
					 * builder.append(line);
					 * builder.append(System.getProperty("line.separator")); }
					 * builder.toString();
					 * System.out.println(builder.toString());
					 */
					
			/*		Process process1 = Runtime.getRuntime().exec(finalCommand);
				        System.out.println("the output stream is "+process1.getOutputStream());
				        BufferedReader reader=new BufferedReader( new InputStreamReader(process1.getInputStream()));
				        String s; 
				        while ((s = reader.readLine()) != null){
				            System.out.println("The inout stream is " + s);
				            builder.append(s);
				        }   
				 */
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println(e);
				}

				break;
			default:
				try {
					String[] commandyash = { "sh", "-c",
							"echo " + Starter.dicConfig.get("SystemUserPassword").toString() + "| sudo -S chmod 777 "
									+ new File(".").getCanonicalPath() + "/Artifacts/CLI/OSX/*" };
					runtime.exec(commandyash);
					InputStream in = runtime.exec(command).getInputStream();
					@SuppressWarnings("rawtypes")
					List ls = IOUtils.readLines(in, "utf-8");
					StringWriter swr = new StringWriter();
					IOUtils.writeLines(ls, IOUtils.LINE_SEPARATOR_WINDOWS, swr);
					builder.append(swr.toString());
					
					
					
					
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				logger.logInfo("Output of CLI command executed: " + builder.toString());
				// System.out.println(builder.toString());
				break;

			}
		} catch (Exception e) {
			logger.logError("Error in executing CLI command: " + command + ". Error: " + e.getMessage());
		}
		return builder.toString();
	}

	/**
	 * Method to return Current Time of Execution
	 * 
	 * @author Yash
	 */
	public void setCurrentTimeofExecution() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();
		currentDateAndTime = formatter.format(date);
	}
	
	public String executePowerShellCommands(String ... command)
	{
		StringBuilder output=new StringBuilder();
		try (PowerShell powerShell = PowerShell.openSession()) {
		
		for(String cmnd : command)
		{
			output.append(powerShell.executeCommand(cmnd).getCommandOutput());
		}
		
		return output.toString();
	}  catch(PowerShellNotAvailableException ex) {
	       //Handle error when PowerShell is not available in the system
	       //Maybe try in another way?
		return null;
	      }
   }
	

}
