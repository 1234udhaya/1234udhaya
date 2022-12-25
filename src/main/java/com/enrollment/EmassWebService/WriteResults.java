package com.enrollment.EmassWebService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class WriteResults {

    public static void writeToTextFile(Map<String, Map<String, String>> records) {
        try {
            File file = new File("report.txt");
            BufferedWriter bf = new BufferedWriter(new FileWriter(file));

            records.values().forEach(entryMap -> {
                try {
                    bf.write(entryMap.get("hccIdentifier") + "  " + entryMap.get("response_code") + "   " + entryMap.get("response_body"));
                    bf.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
