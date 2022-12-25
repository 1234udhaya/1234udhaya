package com.enrollment.EmassWebService;

import org.springframework.util.ResourceUtils;
import org.testng.annotations.Test;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@SpringBootTest(classes = EmassWebService.class)
public class EmassWebServiceAPI extends AbstractTestNGSpringContextTests {

    HealthEdge healthEdge = new HealthEdge();


    @Autowired
    Environment environment;

    int maxLimit;

    Logger logger = Logger.getLogger(EmassWebService.class);

    public static Header soapActionHeader = new Header("SOAPAction", "http://healthedge.com/submit");

    @BeforeClass
    public void setupRestAssured() throws Exception {
        healthEdge = new HealthEdge();
        PreemptiveBasicAuthScheme auth = new PreemptiveBasicAuthScheme();
        auth.setUserName(environment.getProperty("user_name"));
        auth.setPassword(environment.getProperty("password"));
        RestAssured.baseURI = environment.getProperty("base_url");
        RestAssured.authentication = auth;
        RestAssured.useRelaxedHTTPSValidation();
        maxLimit = healthEdge.getMaxLimit();
    }

    @BeforeTest
    public void resetWSDL() {
        healthEdge = new HealthEdge();
        logger.info(
                healthEdge.getUserName() + "------" + healthEdge.getPassword() + "0-------" + healthEdge.getBaseUrl());
        for (int i = 1; i <= healthEdge.getMaxLimit(); i++) {
            try {
                if (RestAssured.get(new URI(healthEdge.getWsdlUrl())).getStatusCode() == 200) {
                    break;
                }
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                logger.info("Max limit exhausted");
            }
        }
    }

    @Test
    public void enrollmentSparseTest() throws IOException, SAXException, ParserConfigurationException {
        Map<String, Map<String, String>> map1 = null;// = EmassWebService.records;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        File template_file = ResourceUtils.getFile("classpath:Enrollement_data.xml");
        InputStream is = new FileInputStream(template_file);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        try {
            Iterator iterator = map1.keySet().iterator();
            while (iterator.hasNext()) {
                Map<String, String> d = map1.get((String) iterator.next());
                doc = XMLUpdate.writeXML(d, doc);
                try (FileOutputStream output = new FileOutputStream("src/main/resources/Enrollement_data.xml")) {
                    XMLUpdate.writeXML(doc, output);
                }
                File dataFile = new File("src/main/resources/Enrollement_data.xml");
                Response resp = RestAssured.given().contentType(ContentType.XML).header(soapActionHeader).body(dataFile)
                        .post("EnrollmentSparse");
                System.out.println("Response code :: " + resp.getStatusCode());
                System.out.println("Response body :: \n" + resp.asPrettyString());
                AssertJUnit.assertEquals(200, resp.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
