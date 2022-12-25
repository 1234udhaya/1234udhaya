package com.enrollment.EmassWebService;

import com.enrollment.EmassWebService.dto.PersonalDetails;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class RecordPreparation {

    public PersonalDetails personalDetails;
    public static Map<String, Map<String, String>> records = new HashMap<String, Map<String, String>>();

    public RecordPreparation(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public void HashMapFromTextFile() {
        Map<String, String> map;
        BufferedReader br = null;
        try {
            File file = ResourceUtils.getFile("classpath:eligxmit_data.txt");
            br = new BufferedReader(new FileReader(file));
            String line = null;
            line = br.readLine();
            while (!"".equals(line) && line != null) {
                map = records.get(line.substring(3, 11)) == null ? new HashMap<String, String>() : records.get(line.substring(3, 11));
                if (line != null && !line.trim().isEmpty()) {
                    Map<String, String> map2 = processLine(line);
                    Iterator iterator = map2.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        map.put(key, map2.get(key));
                    }
                    records.put(line.substring(3, 11), map);
                }
                line = br.readLine();
            }
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

    private Map<String, String> processLine(@NonNull String s) {
        if (s != null) {
            Map<String, String> map = new HashMap<>();
            char c = s.charAt(0);
            switch (c) {
                case 'D':
                    map = personalDetails.getDetails(s);
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
        //System.out.println("details :: " + map);
        return map;
    }

    // AUTHREP -‘R’:
    private static Map<String, String> readAuthrep(String content) {
        int leng = content.length();
        String BASE_ID = content.substring(3, 11);
        String lastName = content.substring(19, 28);
        String firstName = content.substring(29, leng-1);
        String middleName = "";
        if (leng > 41) middleName = content.substring(41, 42);
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
}
