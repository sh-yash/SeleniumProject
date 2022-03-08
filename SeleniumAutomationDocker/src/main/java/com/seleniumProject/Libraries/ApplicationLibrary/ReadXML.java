package com.seleniumProject.Libraries.ApplicationLibrary;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.seleniumProject.Utils.Logging;

public class ReadXML {

	private static Logging logger = new Logging();

	/**
	 * This function searches for the object in the OR XML file
	 * 
	 * @author
	 * @throws Exception
	 * @since
	 */
	public HashMap<String, String> getObjectProperty(String strFile, String strTestScriptID) throws Exception {

		HashMap<String, String> hashMap = new HashMap<String, String>();
		HashMap<String, String> hashMap2 = new HashMap<String, String>();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		String strFileXMLLocation = new File(".").getCanonicalPath() + strFile;
		Document document = docBuilder.parse(strFileXMLLocation);
		NodeList nodelist = document.getDocumentElement().getChildNodes();
		for (int k = 0; k < nodelist.getLength(); k++) {
			hashMap2 = printTags((Node) nodelist.item(k), hashMap, strTestScriptID);
		}
		return hashMap2;

	}

	public HashMap<String, String> printTags(Node nodes, HashMap<String, String> hashMap, String strTestScriptID) {

		try {
			boolean blnFlag = false;
			if (nodes.hasChildNodes() || nodes.getNodeType() != 3) {
				NodeList nodelist = nodes.getChildNodes();
				for (int j = 0; j < nodelist.getLength(); j++) {
					Node node = nodelist.item(j);
					if (nodes.getNodeName().equals("TestName")) {
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							if (nodes.hasAttributes()) {
								// get attributes names and values
								NamedNodeMap nodeMap = nodes.getAttributes();
								for (int i = 0; i < nodeMap.getLength(); i++) {
									Node tempNode = nodeMap.item(i);
									if (tempNode.getNodeValue().equals(strTestScriptID)) {
										blnFlag = true;
										// System.out.println("Attr name : " +
										// tempNode.getNodeName()+ "; Value = "
										// + tempNode.getNodeValue());
										break;
									}
								}
							}
							j++;

						}
						if (blnFlag & j > 1 & !node.getNodeName().equals("TestName")) {
							hashMap.put(node.getNodeName(), node.getTextContent());
							printTags(nodelist.item(j), hashMap, strTestScriptID);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.logError(e.getMessage());
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
		return hashMap;
	}
}
