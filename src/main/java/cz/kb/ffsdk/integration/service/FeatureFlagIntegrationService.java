package cz.kb.ffsdk.integration.service;

import cz.kb.ffsdk.integration.dto.ChecksumDTO;
import cz.kb.ffsdk.integration.dto.ConfigurationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureFlagIntegrationService {

    private final static RestTemplate restTemplate = new RestTemplate();

    public static String domain;
    public static String applicationId;

    @Value("${ffsdk.application.rest_service.domain}")
    private void setDomain(String domain) {
        FeatureFlagIntegrationService.domain = domain;
    }

    @Value("${ffsdk.application.id}")
    private void setApplicationId(String applicationId) {
        FeatureFlagIntegrationService.applicationId = applicationId;
    }

    public static ConfigurationDTO getConfiguration(String featureName) {
        HttpEntity<String> entity = getEntityHeadersWithApiKey();

        ResponseEntity<ConfigurationDTO> response;
        try {
            response = restTemplate.exchange(
                    domain + featureName, HttpMethod.GET, entity, ConfigurationDTO.class);
        } catch (RestClientException e) {
            log.error("Connection problem");
            return null;
        }


        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            log.info("Rest response returns code is " + response.getStatusCode());
            return null;
        }
    }

    public static ChecksumDTO getCheckSum(String featureName) {
        HttpEntity<String> entity = getEntityHeadersWithApiKey();

        ResponseEntity<ChecksumDTO> response;
        try {
            response = restTemplate.exchange(
                    domain + featureName + "/checksum", HttpMethod.GET, entity, ChecksumDTO.class);
        } catch (RestClientException e) {
            log.error("Connection problem");
            return null;
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            log.info("Rest response for checksum returns code" + response.getStatusCode());
            return null;
        }
    }

    private static HttpEntity<String> getEntityHeadersWithApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", applicationId);
        return new HttpEntity<>(headers);
    }
}
