package com.enrollment.EmassWebService;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

@Service
public class RecordProcessing {

    public HealthEdge healthEdge;
    public Logger logger = Logger.getLogger(EmassWebService.class);

    public RecordProcessing(HealthEdge healthEdge) throws Exception {
        this.healthEdge = healthEdge;
        setupRestAssured();
    }

    public static Header soapActionHeader = new Header("SOAPAction", "http://healthedge.com/submit");

    public void setupRestAssured() throws Exception {
        PreemptiveBasicAuthScheme auth = new PreemptiveBasicAuthScheme();
        auth.setUserName(healthEdge.getUserName());
        auth.setPassword(healthEdge.getPassword());
        RestAssured.baseURI = healthEdge.getBaseUrl();
        RestAssured.authentication = auth;
        RestAssured.useRelaxedHTTPSValidation();
    }

    public void resetWSDL() {
        for (int i = 1; i <= healthEdge.getMaxLimit(); i++) {
            System.out.println("Times :: " + i);
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

    public void process(Map<String, Map<String, String>> records) {
        records.entrySet().parallelStream().forEach(stringMapEntry -> {
            System.out.println(stringMapEntry.getValue().get("hccIdentifier"));
        });
    }

    public Map<String, Map<String, String>> processEntrollment(Map<String, Map<String, String>> records) throws IOException, SAXException, ParserConfigurationException {
        try {
            records.entrySet().parallelStream().forEach(stringMapEntry -> {
                Map<String, String> value = stringMapEntry.getValue();

                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    File template_file = ResourceUtils.getFile("classpath:Enrollement_data.xml");
                    InputStream is = new FileInputStream(template_file);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(is);
                    doc = XMLUpdate.writeXML(value, doc);
                    String file_name = "data_files/Enrollement_data_"+value.get("hccIdentifier")+".xml";
                    try (FileOutputStream output = new FileOutputStream(file_name)) {
                        XMLUpdate.writeXML(doc, output);
                    }
                    File dataFile = new File(file_name);
                    resetWSDL();
                    Response resp = RestAssured.given().contentType(ContentType.XML).header(soapActionHeader).body(dataFile)
                            .post("EnrollmentSparse");
                    System.out.println("Response code :: " + resp.getStatusCode());
                    System.out.println("Response body :: \n" + resp.asPrettyString());
                    value.put("response_code", String.valueOf(resp.getStatusCode()));
                    value.put("response_body", resp.body().asString());
                    //value.put("response_code", "100");
                    //value.put("response_body", "Sample");
                    records.put(stringMapEntry.getKey(), value);
                    Files.deleteIfExists(dataFile.toPath());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }
}
