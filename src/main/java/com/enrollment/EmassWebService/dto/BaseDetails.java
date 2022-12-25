package com.enrollment.EmassWebService.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@EnableConfigurationProperties
public class BaseDetails {

    @Value("${common.base_id}")
    public int[] base_id;

    public String getGender(String content) {
        String gender = content.substring(content.length() - 1);
        String gen;
        if ("1".equals(gender)) {
            gen = "M";
        } else if ("2".equals(gender)) {
            gen = "F";
        } else {
            gen = "U";
        }
        return gen;
    }
}
