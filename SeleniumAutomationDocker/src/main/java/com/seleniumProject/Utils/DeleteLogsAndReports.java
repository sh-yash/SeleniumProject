package com.seleniumProject.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * @author Yash
 * @lastModified- 24-02-2020 by Yash
 *
 */
public class DeleteLogsAndReports {

	private static Logging logger = new Logging();

	public boolean deleteFiles(String folder) {

		logger.logInfo("Deleting files/folders from " + folder + " directory");
		try {
			ArrayList<File> listOfFiles = new ArrayList<>();
			listOfFiles = getListOfFiles(new File(folder));

			if (listOfFiles.size() < 6) {

				logger.logInfo("Only " + listOfFiles.size() + " are present in " + folder
						+ " folder. Therefore skipping deleting file(s)/folder(s). ");
				return true;
			}

			Collections.sort(listOfFiles, getReverseLastModifiedComparator());
			delete(listOfFiles);
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	private static void delete(ArrayList<File> listOfFiles) {

		File currentFile = null;

		try {
			for (File file : listOfFiles) {

				currentFile = file;
				if (listOfFiles.get(listOfFiles.size() - 5).equals(file))
					break;

				if (file.isDirectory())
					deleteNestedFiles(file);

				else
					file.delete();

				logger.logInfo(file.toString() + ": deleted successfully");
			}
		} catch (Exception e) {
			logger.logError("Error occurred in deleting file: " + currentFile.toString(), e);
		}
	}

	public static ArrayList<File> getListOfFiles(File folder) {
		ArrayList<File> listOfFiles = new ArrayList<>();
		File[] files = folder.listFiles();
		try {
			for (int i = 0; i < files.length; i++) {
				listOfFiles.add(files[i]);
			}
			return listOfFiles;
		} catch (Exception e) {
			logger.logError("Error occurred retrieving file names.", e);
			return null;
		}
	}

	public static void deleteNestedFiles(File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteNestedFiles(f);
			}
		}
		file.delete();
	}
	
	
	
	public static String lastmodifiedFilePath(String folder)
	{
		try {
			ArrayList<File> listOfFiles = new ArrayList<>();
			listOfFiles = getListOfFiles(new File(folder));

			if (listOfFiles.size() < 0) {

				logger.logInfo("Only " + listOfFiles.size() + " are present in " + folder
						+ " folder. Therefore skipping deleting file(s)/folder(s). ");
				return null;
			}

			Collections.sort(listOfFiles, getReverseLastModifiedComparator());
			
			if(listOfFiles.get(listOfFiles.size()-1).isDirectory())
			{
				for (File f : listOfFiles.get(listOfFiles.size()-1).listFiles()) {
					if(f.getAbsolutePath().contains(".htm"))
						return f.getAbsolutePath();
				}
			}
			else
			{
				return listOfFiles.get(listOfFiles.size()-1).getAbsolutePath();
			}
		} catch (Exception e) {

			return null;
		}
		return null;
	}

	private static Comparator<File> getReverseLastModifiedComparator() {

		return (File o1, File o2) -> {

			if (o1.lastModified() < o2.lastModified()) {
				return -1;
			}
			if (o1.lastModified() > o2.lastModified()) {
				return 1;
			}
			return 0;
		};
	}

	public void deleteVideoFiles() {
		String path = System.getProperty("user.dir" + "\\Report\\Videos\\");
		File dir = new File(path);
		File[] files = dir.listFiles();
		logger.logInfo("Deleting video files from the video folder");
		for (File file : files) {
			file.delete();
		}

	}
}
