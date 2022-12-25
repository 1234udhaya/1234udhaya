package com.enrollment.EmassWebService;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNull;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@SpringBootApplication
public class EmassWebService implements CommandLineRunner {

    public static Map<String, Map<String, String>> map1 = new HashMap<String, Map<String, String>>();

    public static void main(String[] args) {
        SpringApplication.run(EmassWebService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        HashMapFromTextFile();
        // System.out.println(getDetails);
        // XMLUpdate.writeXML(getDetails);
    }

    public static void HashMapFromTextFile() {
        Map<String, String> map;
        BufferedReader br = null;
        try {
            File file = ResourceUtils.getFile("classpath:eligxmit_data.txt");
            br = new BufferedReader(new FileReader(file));
            String line = null;
            line = br.readLine();
            while (!"".equals(line) && line != null) {
                System.out.println(line.substring(3, 11));
                map = map1.get(line.substring(3, 11)) == null ? new HashMap<String, String>() : map1.get(line.substring(3, 11));
                if (line != null && !line.trim().isEmpty()) {
                    System.out.println(line.length());
                    Map<String, String> map2 = processLine(line);
                    Iterator iterator = map2.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        map.put(key, map2.get(key));
                    }
                    map1.put(line.substring(3, 11), map);
                    System.out.println(map1);
                    //doc = XMLUpdate.writeXML(map, doc);
                }
                line = br.readLine();
            }
            System.out.println("control here");
            /*Iterator iterator = map1.keySet().iterator();
            while(iterator.hasNext()) {
                Map<String, String> map3 = (Map<String, String>) iterator.next();
                doc = XMLUpdate.writeXML(map3, doc);
                try (FileOutputStream output = new FileOutputStream("src/main/resources/Enrollement_data.xml")) {
                    XMLUpdate.writeXML(doc, output);
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }

    }

    private static Map<String, String> processLine(@NonNull String s) {

        if (s != null) {
            Map<String, String> map = new HashMap<>();
            char c = s.charAt(0);
            System.out.println(c);
            switch (c) {
                case 'D':
                    map = readPersonDetails(s);
                    break;
                case 'B':
                    map = readBaseDetails(s);
                    break;
                case 'R':
                    map = readAuthrep(s);
                    break;
                case 'E':
                    map = readELigDetails(s);
                    break;
                case 'L':
                    map = readLockDetails(s);
                    break;
            }
            return map;
        }
        // String map = null;
        // System.out.println("_____________" + map);
        return new HashMap<>();
    }

    // LOCK -"L":
    private static Map<String, String> readLockDetails(String content) {
        String BASE_ID = content.substring(3, 11);
        String startDate = content.substring(38, 48);
        String endDate = content.substring(50, 60);
        // String complianceCode = content.substring(60, 61);
        Map<String, String> map = new HashMap<>();
        map.put("hccIdentifier", BASE_ID.trim());
        map.put("complianceProgramDateRanges/startDate", startDate.trim());
        map.put("complianceProgramDateRanges/endDate", endDate.trim());
        // map.put("complianceCode", complianceCode.trim());
        return map;
    }

    // ELIG -'E':
    private static Map<String, String> readELigDetails(String content) {
        String BASE_ID = content.substring(3, 11);
        String meCode = content.substring(27, 29);
        Map<String, String> map = new HashMap<>();
        map.put("hccIdentifier", BASE_ID.trim());
        map.put("attrValueAsString", "ME Code " + meCode.trim());
        System.out.println("details :: " + map);
        return map;
    }

    // AUTHREP -‘R’:
    private static Map<String, String> readAuthrep(String content) {

        String BASE_ID = content.substring(3, 11);
        String lastName = content.substring(19, 28);
        String firstName = content.substring(29, 41);
        String middleName = content.substring(41, 42);
        Map<String, String> map = new HashMap<>();
        map.put("hccIdentifier", BASE_ID.trim());
        map.put("responsiblePersonName/firstName", firstName.trim());
        map.put("responsiblePersonName/lastName", lastName.trim());
        map.put("responsiblePersonName/middleName", middleName.trim());
        return map;
    }

    // BASE -'B':
    private static Map<String, String> readBaseDetails(String content) {

        String BASE_ID = content.substring(3, 11);
        String FIRST_LINE = content.substring(84, 109);
        String address2 = content.substring(59, 84);
        String State = content.substring(131, 133);
        String zip = content.substring(133, 139);
        String CITY = content.substring(109, 131);
        // String county = content.substring(140, 142);
        // String phone = content.substring(142, 152);
        Map<String, String> map = new HashMap<>();
        map.put("hccIdentifier", BASE_ID.trim());
        map.put("address", FIRST_LINE.trim());
        map.put("address2", address2.trim());
        map.put("stateCode", State.trim());
        map.put("zipCode", zip.trim());
        map.put("cityName", CITY.trim());
        // map.put("countyCode", county.trim());
        // map.put("phoneNumber", phone.trim());
        return map;
    }

    // DEMO -'D':
    private static Map<String, String> readPersonDetails(String content) {
        String BASE_ID = content.substring(3, 11);
        String lastName = content.substring(19, 38);
        String firstName = content.substring(38, 50);
        String middleName = content.substring(50, 51);
        String dob = content.substring(51, 61);
        // String ssn = content.substring(78, 89);
        String codeEntry = content.substring(88, 89);
        String gender = content.substring(content.length() - 1);
        String gen;
        if ("1".equals(gender)) {
            gen = "M";
        } else if ("2".equals(gender)) {
            gen = "F";
        } else {
            gen = "U";
        }

        Map<String, String> map = new HashMap<>();
        // System.out.println(gender);
        map.put("hccIdentifier", BASE_ID.trim());
        map.put("primaryName/firstName", firstName.trim());
        map.put("primaryName/lastName", lastName.trim());
        map.put("primaryName/middleName", middleName.trim());
        map.put("birthDate", dob.trim());
        // map.put("taxIdentificationNumber", ssn.trim());
        map.put("raceOrEthnicityCodes/codeEntry", codeEntry.trim());
        map.put("genderCode", gen);
        return map;
    }

    /*
     * public static Map<String, String> HashMapFromTextFile() { Map<String, String>     * map = new HashMap<String, String>(); BufferedReader br = null; try { //File
     * file = ResourceUtils.getFile("classpath:Enrollement_data.txt"); File file =
     * new File("C:\\Users\\ukumar\\Desktop\\XML UPDATE\\Enrollement_data.txt"); br
     * = new BufferedReader(new FileReader(file)); String line = null; while ((line
     * = br.readLine()) != null) { String key = line.split(":")[0].trim(); String
     * value = line.split(":")[1].trim();
     *
     * if (!key.equals("") && !key.equals("")) map.put(key, value); } } catch
     * (Exception e) { e.printStackTrace(); } finally { if (br != null) { try {
     * br.close(); } catch (Exception e) { } } } return map; }
     */
}