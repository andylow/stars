package com.siberhus.stars.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JndiNameRefMap {

	private Map<String, String> EMF_MAP = new HashMap<String, String>();

	private Map<String, String> EM_MAP = new HashMap<String, String>();
	
	public JndiNameRefMap(InputStream webXmlInputStream) throws IOException, SAXException, ParserConfigurationException {
		
		Document document = null;
		try{
			document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(webXmlInputStream);
		}finally{
			webXmlInputStream.close();
		}
		NodeList nodeList = document
				.getElementsByTagName("persistence-context-ref");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node persistenceContext = nodeList.item(i);
			NodeList childNodes = persistenceContext.getChildNodes();
			String unitName = null, refName = null;
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = childNodes.item(j);
				if (childNode.getNodeType() == Node.TEXT_NODE)
					continue;
				if ("persistence-context-ref-name".equals(childNode
						.getNodeName())) {
					refName = childNode.getTextContent();
				} else if ("persistence-unit-name".equals(childNode
						.getNodeName())) {
					unitName = childNode.getTextContent();
				}
			}
			setEntityManagerName(unitName, refName);
		}
		nodeList = document.getElementsByTagName("persistence-unit-ref");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node persistenceUnit = nodeList.item(i);
			NodeList childNodes = persistenceUnit.getChildNodes();
			String unitName = null, refName = null;
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = childNodes.item(j);
				if (childNode.getNodeType() == Node.TEXT_NODE)
					continue;
				if ("persistence-unit-ref-name".equals(childNode.getNodeName())) {
					refName = childNode.getTextContent();
				} else if ("persistence-unit-name".equals(childNode
						.getNodeName())) {
					unitName = childNode.getTextContent();
				}
			}
			setEntityManagerFactoryName(unitName, refName);
		}
	}

	public void setEntityManagerFactoryName(String unitName, String jndiName) {
		EMF_MAP.put(unitName, jndiName);
	}

	public String getEntityManagerFactoryJndiName(String unitName) {
		return EMF_MAP.get(unitName);
	}

	public String getEntityManagerFactoryJndiName() {
		return EMF_MAP.get("default");
	}

	public void setEntityManagerName(String unitName, String jndiName) {
		EM_MAP.put(unitName, jndiName);
	}

	public String getEntityManagerJndiName(String unitName) {
		return EM_MAP.get(unitName);
	}

	public String getEntityManagerJndiName() {
		return EM_MAP.get("default");
	}

}
