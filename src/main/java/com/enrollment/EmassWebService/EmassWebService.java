package com.enrollment.EmassWebService;

import com.enrollment.EmassWebService.dto.PersonalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Map;

@SpringBootApplication
public class EmassWebService implements CommandLineRunner {

    @Autowired
    HealthEdge healthEdge = new HealthEdge();

    @Autowired
    Environment environment;

    @Autowired
    PersonalDetails personalDetails = new PersonalDetails();

    public static void main(String[] args) {
        SpringApplication.run(EmassWebService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RecordPreparation recordPreparation = new RecordPreparation(personalDetails);
        recordPreparation.HashMapFromTextFile();

        RecordProcessing recordProcessing = new RecordProcessing(healthEdge);
        Map<String, Map<String, String>> processedRecords = recordProcessing.processEntrollment(RecordPreparation.records);

        WriteResults.writeToTextFile(processedRecords);
        System.exit(1);
    }
}