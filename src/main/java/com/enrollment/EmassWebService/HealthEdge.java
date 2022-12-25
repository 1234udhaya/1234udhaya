package com.enrollment.EmassWebService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
public class HealthEdge {
	
	@Value("${max_limit}")
	private  int maxLimit;
	
	@Value("${user_name}")
	private  String userName;
	
	@Value("${password}")
	private  String password;
	
	@Value("${base_url}")
	private  String baseUrl;
	
	@Value("${wsdl_url}")
	private  String wsdlUrl;

	public  int getMaxLimit() {
		return maxLimit;
	}

	public  void setMaxLimit(int maxLimit) {
		this.maxLimit = maxLimit;
	}

	public  String getUserName() {
		return userName;
	}

	public  void setUserName(String userName) {
		this.userName = userName;
	}

	public  String getPassword() {
		return password;
	}

	public  void setPassword(String password) {
		this.password = password;
	}

	public  String getBaseUrl() {
		return baseUrl;
	}

	public  void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public  String getWsdlUrl() {
		return wsdlUrl;
	}

	public  void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}
	
	
}
