package com.corebankingsystem.AccountMs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final RestTemplate restTemplate;

    @Autowired
    public CustomerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isValidCustomer(Long customerId) {
        try {
            String url = "http://localhost:8086/customers/" + customerId;
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (HttpClientErrorException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while validating the customer ID");
        }
    }
}
