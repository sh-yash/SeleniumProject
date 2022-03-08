package com.seleniumProject.Libraries.SeleniumLibrary;



import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import com.googlecode.fightinglayoutbugs.FightingLayoutBugs;
import com.googlecode.fightinglayoutbugs.LayoutBug;
import com.googlecode.fightinglayoutbugs.WebPage;
import com.seleniumProject.Utils.DeleteLogsAndReports;
import com.seleniumProject.Utils.Logging;

public class BaseUtility {
	private static Logging logger = new Logging();

	/**
	 * @author Vikas Rathour 
	 * This Method is used to copy the text
	 */
	public String copyText(String text) {
		try {
			logger.logInfo("Creating copy of text");
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection str = new StringSelection(text);
			clipboard.setContents(str, null);
		} catch (HeadlessException e) {
			logger.logError("Something went wrong during copy text" + e.getMessage());
		}
		return text;
	}

	public float getTime() {
		DateFormat df = new SimpleDateFormat("HH mm ss SSS");
		String mmss = df.format(new Date());
		return (float) Long.parseUnsignedLong(mmss.split(" ")[0]) * 3600
				+ Long.parseUnsignedLong(mmss.split(" ")[1]) * 60 + Long.parseUnsignedLong(mmss.split(" ")[2])
				+ Long.parseUnsignedLong(mmss.split(" ")[2]) * 0.001f;
	}

	/**
	 * @author - Vikas
	 * 
	 * @param date : This method sort the date in Acceding order
	 */

	public boolean verifySortByDateAccendingOrder(List<String> date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		boolean accendingOrder = true;
		for (int index = 0; index < date.size() - 1; index++) {
			try {
				if (simpleDateFormat.parse(date.get(index)).getTime() > simpleDateFormat.parse(date.get(index + 1))
						.getTime()) {
					accendingOrder = false;
					break;
				}
			} catch (ParseException e) {
				logger.logError("Something went wrong" + e.getMessage());
			}
		}
		if (accendingOrder) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @author - Vikas
	 * 
	 * @param date : This method sort the date in descending order
	 */

	public boolean verifySortByDateDecendingOrder(List<String> date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		boolean decendingOrder = true;
		for (int index = 0; index < date.size() - 1; index++) {
			try {
				if (simpleDateFormat.parse(date.get(index)).getTime() < simpleDateFormat.parse(date.get(index + 1))
						.getTime()) {
					decendingOrder = false;
					break;
				}
			} catch (ParseException e) {
				logger.logError("Something went wrong" + e.getMessage());
			}
		}

		if (decendingOrder) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @author Vikas - > This method is used to Save the data in a File
	 * 
	 * @param fileName : Data to be save in which file
	 * @param data     : Data to be saved in file
	 */
	public void saveResultInFile(String fileName, String data) {
		String filePath = System.getProperty("user.dir");
		String path = filePath + "\\Artifacts\\InFiles\\";
		File log = new File(path + fileName + ".txt");
		try {
			if (log.exists() == false) {
				log.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(log, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(data + "" + " , " + "  " + " ");
			bufferedWriter.close();
			logger.logInfo("Data write in file successfully");
		} catch (IOException e) {
			logger.logError("COULD NOT WRITE!!" + e.getMessage());

		}
	}

	/**
	 * Method to wait for the current file to be downloaded
	 * 
	 * @author : Yash .
	 * @return void
	 * @throws IOException
	 */
	public void waitForFileDownload() throws IOException {
		File folder = null;

		folder = new File(new File(".").getCanonicalFile() + "/Artifacts/OutFiles/");
		int currNumOfFiles = 0;
		while (true) {
			currNumOfFiles = 0;
			for (File f : DeleteLogsAndReports.getListOfFiles(folder)) {
				if (f.getName().contains(".crdownload") || f.getName().contains(".part")
						|| f.getName().contains(".download"))
					currNumOfFiles++;
			}
			if (currNumOfFiles == 0)
				return;
		}
	}

	/**
	 * author @yash Unzipping the zip cli windows
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */

	public static boolean unzip(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		// create output directory if it doesn't exist

		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("Unable to Zip File");

		}

	}

	/**
	 * author @yash Unzipping the zip cli windows
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */
	public ArrayList<String> getColumnValueFromCSV(String file, String columnName) {
		ArrayList<String> value = new ArrayList<String>();

		String line = "";
		boolean flag = false;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int i = 0;
			int columnIndex = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (!flag) {
					for (String val : values) {
						if (i == 0 && val.equals("\"" + columnName + "\"")) {
							flag = true;
							break;
						} else if (i == 0) {
							columnIndex++;
						}
					}
				} else {
					if (!values[0].contains("Exception")) {
						value.add(values[columnIndex].replace("\"", ""));
					}
				}
				i++;
			}

		} catch (IOException e) {

			System.err.println("Something went wrong" + e.getMessage());
		}
		return value;
	}

	/**
	 * author @yash Unzipping the zip cli windows
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */
	private void unTar() throws Exception {
		String[] INPUT_FILE = { new File(".").getCanonicalPath() + "/Artifacts/CLI/OSX/dc-osx-x64.tar.gz",
				new File(".").getCanonicalPath() + "/Artifacts/CLI/Portable/dc-portable-x64.tar.gz" };
		String[] TAR_FOLDER = { new File(".").getCanonicalPath() + "/Artifacts/CLI/OSX/",
				new File(".").getCanonicalPath() + "/Artifacts/CLI/Portable/" };
		String[] DESTINATION_FOLDER = { new File(".").getCanonicalPath() + "/Artifacts/CLI/OSX/",
				new File(".").getCanonicalPath() + "/Artifacts/CLI/Portable/" };
		int var = 0;
		for (String fileName : INPUT_FILE) {
			try {
				File inputFile = new File(fileName);
				String outputFile = getFileName(inputFile, TAR_FOLDER[var]);
				File tarFile = new File(outputFile);
				tarFile = deCompressGZipFile(inputFile, tarFile);
				File destFile = new File(DESTINATION_FOLDER[var]);
				if (!destFile.exists()) {
					destFile.mkdir();
				}
				unTarFile(tarFile, destFile);
				var++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * author @yash Unzipping the zip cli windows
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */
	private void unTarFile(File tarFile, File destFile) throws IOException {
		FileInputStream fis = new FileInputStream(tarFile);
		TarArchiveInputStream tis = new TarArchiveInputStream(fis);
		TarArchiveEntry tarEntry = null;

		while ((tarEntry = tis.getNextTarEntry()) != null) {
			File outputFile = new File(destFile + File.separator + tarEntry.getName());

			if (tarEntry.isDirectory()) {
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}
			} else {
				outputFile.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(tis, fos);
				fos.close();
			}
		}
		tis.close();
	}

	/**
	 * author @yash Unzipping the zip cli windows
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */
	private File deCompressGZipFile(File gZippedFile, File tarFile) throws IOException {
		FileInputStream fis = new FileInputStream(gZippedFile);
		GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);

		FileOutputStream fos = new FileOutputStream(tarFile);
		IOUtils.copy(gZIPInputStream, fos);

		fos.close();
		gZIPInputStream.close();
		return tarFile;

	}

	/**
	 * author @yash get FileName
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @return
	 * 
	 */
	private static String getFileName(File inputFile, String outputFolder) {
		return outputFolder + File.separator + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
	}

	/**
	 *@author Vikas
	 *@param It will accept PDF URL path
	 *@return It will return the text present on the PDF file
	 * 
	 */
//	public String ReadPDF(String path) throws Exception {
//		URL TestURL = new URL(path);
//		BufferedInputStream TestFile = new BufferedInputStream(TestURL.openStream());
//		PDFParser TestPDF = new PDFParser(TestFile);
//		TestPDF.parse();
//		String TestText = new PDFTextStripper().getText(TestPDF.getPDDocument());
//		return TestText;
//
//	}

	/**
	 *@author Vikas
	 *@param It will accept WebDriver object
	 *@return It will return the GUI Related issues
	 * 
	 */
	public ArrayList<LayoutBug> getGUIBugReport(WebDriver driver) {
		ArrayList<LayoutBug> bugType = new ArrayList<LayoutBug>();
		WebPage webPage = new WebPage(driver);
		FightingLayoutBugs flb = new FightingLayoutBugs();
		final Collection<LayoutBug> layoutBugs = flb.findLayoutBugsIn(webPage);
		Reporter.log("Found " + layoutBugs.size() + " layout bug(s).");
		for (LayoutBug bug : layoutBugs) {
			bugType.add(bug);
		}
		return bugType;
	}

	public boolean verifySortByNameDecendingOrder(ArrayList<String> data) {
		boolean isSorted = true;
		for (int i = 0; i < data.size() - 1; i++) {
			if (data.get(i).compareToIgnoreCase(data.get(i + 1)) < 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}

	public boolean verifySortByNameAccendingOrder(ArrayList<String> data) {
		boolean isSorted = true;
		for (int i = 0; i < data.size() - 1; i++) {
			if (data.get(i).compareToIgnoreCase(data.get(i + 1)) > 0) {
				isSorted = false;
				break;
			}
		}
		return isSorted;
	}
	
}
