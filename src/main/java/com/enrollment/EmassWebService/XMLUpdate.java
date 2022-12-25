package com.enrollment.EmassWebService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUpdate {

    public static Document writeXML(Map<String, String> map, Document doc) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String parentKey = key.contains("/") ? key.split("/")[0] : key;
            if (parentKey.equals(key)) {
                NodeList listOfElements = doc.getElementsByTagName(entry.getKey());
                for (int i = 0; i < listOfElements.getLength(); i++) {
                    Node node = doc.getElementsByTagName(key).item(i);
                    if (node != null)
                        doc.getElementsByTagName(key).item(i).setTextContent(entry.getValue());
                }
            } else {
                String childKey = key.split("/")[1];
                NodeList nodeList = doc.getElementsByTagName(parentKey);
                if (nodeList.getLength() > 1) {
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NodeList subNodeList = nodeList.item(i).getChildNodes();
                        for (int j = 0; j < subNodeList.getLength(); j++) {
                            if (subNodeList.item(j).getNodeName().equals(childKey)) {
                                subNodeList.item(j).setTextContent(entry.getValue());
                            }
                        }
                    }
                } else {
                    NodeList subNodeList = nodeList.item(0).getChildNodes();
                    for (int i = 0; i < subNodeList.getLength(); i++) {
                        if (subNodeList.item(i).getNodeName().equals(childKey)) {
                            subNodeList.item(i).setTextContent(entry.getValue());
                        }
                    }
                }

            }

        }
        return doc;
    }

    public static void writeXML(Document doc, FileOutputStream output)
            throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        // System.out.println("-----------Modified File-----------");
        StreamResult consoleResult = new StreamResult(output);
        transformer.transform(source, consoleResult);
    }
}
