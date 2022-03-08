package com.seleniumProject.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.gargoylesoftware.htmlunit.WebConsole.Logger;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListImagesParam;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.Image;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.ProgressMessage;
import com.seleniumProject.Libraries.CLILibrary.CLICommandExecutor;
import com.seleniumProject.Runner.Starter;

public class Containers {
	public final DockerClient docker = new DefaultDockerClient("unix:///var/run/docker.sock");
	static int seleniumPort = 4444;
	static int vncPort = 5901;
	static int seleniumPort1 = 4444;
	static int vncPort1 = 5900;
	static int containerId = 1;
	Logging logger=new Logging();
	CLICommandExecutor executor= new CLICommandExecutor();

	/**
	 * Created by Yash . It will start the single Selenium container instance
	 * 
	 * @param dockerImage
	 * @return
	 */
	public boolean startDockerInstance(String dockerImage) {
		boolean flag = false;
		try {
			final String[] ports = { String.valueOf(seleniumPort), String.valueOf(vncPort) };
			final Map<String, List<PortBinding>> portBindings = new HashMap<>();

			List<PortBinding> hostSeleniumPorts = new ArrayList<>();
			hostSeleniumPorts.add(PortBinding.of("0.0.0.0", ports[0]));
			portBindings.put("4444/tcp", hostSeleniumPorts);

			List<PortBinding> hostVNCPorts = new ArrayList<>();
			hostVNCPorts.add(PortBinding.of("0.0.0.0", ports[1]));
			portBindings.put("5900/tcp", hostVNCPorts);

			final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings)
					.appendBinds(new File(".").getCanonicalPath() + "/Artifacts/:/src").build();

			final ContainerConfig config = ContainerConfig.builder().hostConfig(hostConfig).image(dockerImage)
					.exposedPorts(ports).build();
			final ContainerCreation container = docker.createContainer(config,
					"selenium-standalone-container-" + containerId);
			docker.startContainer(container.id());
			flag = true;
			seleniumPort++;
			vncPort++;
			containerId++;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * Created by Yash . Function to check whether given image exist or not
	 * 
	 * @param imageName
	 * @return
	 */
	public boolean isImageExist(String imageName) {
		boolean flag = false;
		try {
			final List<Image> quxImages = docker.listImages(ListImagesParam.allImages());
			for (Image im : quxImages) {
				if (im.repoTags().contains(imageName)) {
					System.out.println("RepoTags: " + im.repoTags());
					flag = true;
				}
			}
		} catch (DockerException e) {
			flag = false;
			e.printStackTrace();
		} catch (InterruptedException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * Created by Yash . This function will build image from Docker file for
	 * single selenium container
	 */
	public void buildImageFromDockerFile(String imageName, String path) {
		final AtomicReference<String> imageIdFromMessage = new AtomicReference<>();
		try {
			docker.build(Paths.get(new File(".").getCanonicalPath() + path), imageName, new ProgressHandler() {
				@Override
				public void progress(ProgressMessage message) throws DockerException {
					final String imageId = message.buildImageId();
					if (imageId != null) {
						imageIdFromMessage.set(imageId);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Created by Yash .. To stop and remove container.
	 * 
	 * @return
	 */
	public boolean stopContainers() {
		boolean flag = false;
		try {
			final List<Container> containers = docker.listContainers();
			for (Container im : containers) {
				docker.stopContainer(im.id(), 2);
				docker.removeContainer(im.id());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * Created by Yash .. Function will start the Selenium container instances,
	 * can be used for debugging and script writing purpose.
	 * 
	 * @param imageName
	 * @return
	 */
	public boolean startContainers(String imageName, Containers ob, String dockerFilePath) {
		boolean flag = false;

		if (!(ob.isImageExist(imageName))) {
			ob.buildImageFromDockerFile(imageName, dockerFilePath);
		}
		if (ob.isImageExist(imageName)) {
			flag = ob.startDockerInstance(imageName);
		}
		return flag;
	}

	/**
	 * Created by Yash .. It will check whether the container is running for the
	 * particular image.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isContainerRunning(String name) {
		boolean flag = false;
		try {
			// Let the container started
			Thread.sleep(5000);
			final List<Container> containers = getRunningContainers();
			for (Container im : containers) {
				if (im.image().contains(name)) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	
	public boolean startDockerMachine(){
		
		
		String command="start_docker";
		logger.logInfo("Executing the cli command for docker start");
		/*if(executor.executeCLICommand(command, "seleniumProject", "xxx","windows 10").contains("Started"))
		{
			logger.logInfo("Docker machine started successfully");
			return true;
		}
		else{
			return true;
		}*/
		return true;
	}
	
	public boolean triggerComposeFile() throws IOException{
		String command="docker-compose -f \""+new File(".").getCanonicalPath()+ "\\docker-compose.yml\" up -d";
		String beforeCommand="@FOR /f \"tokens=*\" %i IN ('docker-machine.exe env --shell cmd') DO @%i";
		
/*	
		if(executor.executeCLICommand(beforeCommand+" && "+command,"seleniumProject", "xxx","windows 10").contains("done"))
		{
			return true;
		}		*/
		if(executor.executePowerShellCommands("start_docker",command).contains(""))
			return true;
		return false;
	}
	
	/**
	 * Created by Yash .. It will return list of running containers.
	 * 
	 * @return
	 */
	public List<Container> getRunningContainers() {
		List<Container> aList = new ArrayList<Container>();
		try {
			aList = docker.listContainers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aList;
	}

	/**
	 * Created by Yash .. It will write the docker-compose.yml file.
	 * 
	 * @param noOfNodes
	 * @param ob
	 * @return
	 */
	public boolean writeDockerComposeFile(Containers ob) {
		boolean flag = false;
		int nodePort = 4577;
		File file = new File("docker-compose.yml");
		FileWriter fr = null;
		BufferedWriter br = null;
		String nodeImageName = "";
		/*switch (Starter.dicConfig.get("browser").toLowerCase()) {
		case "chrome":
			nodeImageName = Starter.dicConfig.get("nodeChromeDockerImage");
			if (!(ob.isImageExist(nodeImageName))) {
				ob.buildImageFromDockerFile(nodeImageName, "/Docker/Selenium_Node_Chrome");
			}
			break;
		case "firefox":
			nodeImageName = Starter.dicConfig.get("nodeFirefoxDockerImage");
			if (!(ob.isImageExist(nodeImageName))) {
				ob.buildImageFromDockerFile(nodeImageName, "/Docker/Selenium_Node_Firefox");
			}
			break;
		default:
			System.out.println("'" + Starter.dicConfig.get("browser").toLowerCase()
					+ "' browser is not available on this machine.");
			return false;
		}*/
		
		try {

			String content = "seleniumhub:\n  image: selenium/hub\n  ports: \n    - 4444:4444\n\n";
			for (int i = 1; i <= Integer.parseInt(Starter.dicConfig.get("nodeChromeContainers")); i++) {
				content += "ChromeNode" + i + ":\n  image: " + Starter.dicConfig.get("nodeChromeDockerImage") + "\n  ports:\n    - " + nodePort
						+ "\n  links:\n    - seleniumhub:hub\n\n";
				nodePort++;
			}
			for (int i = 1; i <= Integer.parseInt(Starter.dicConfig.get("nodeFirefoxContainers")); i++) {
				content += "FireFoxNode" + i + ":\n  image: " + Starter.dicConfig.get("nodeFirefoxDockerImage") + "\n  ports:\n    - " + nodePort
						+ "\n  links:\n    - seleniumhub:hub\n\n";
				nodePort++;
			}
			
			fr = new FileWriter(file);
			br = new BufferedWriter(fr);
			br.write(content);
			flag = true;

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
}
