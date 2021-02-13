package cz.kb.ffsdk.integration.service;

import cz.kb.ffsdk.integration.dto.*;
import cz.kb.ffsdk.integration.exception.IPIsNotInValidFormException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureFlagService {

    private static final Map<String, ConfigurationDTO> featureFlagCachedMap = new HashMap<>();

    public static boolean isFeatureEnabled(String featureName, ContextInDTO context) {
        ConfigurationDTO configuration = getOrLoadConfiguration(featureName);

        if (configuration == null) {
            log.warn("Feature flag does not exists.");
            return false;
        }

        return evaluateFeature(configuration, context);
    }

    private static boolean evaluateFeature(ConfigurationDTO configuration, ContextInDTO context) {

        for (ConditionDTO condition : configuration.getConditions()
        ) {
            if (condition.getType().equals(ConditionType.USER_NAME)
                    && context.getUsername().equals(condition.getValue())) {
                return true;
            }

            if (condition.getType().equals(ConditionType.ENVIRONMENT)
                    && context.getEnvironment().equals(condition.getValue())) {
                return true;
            }

            if (condition.getType().equals(ConditionType.IP_RANGE)) {
                boolean isIPMatch;

                try {
                    isIPMatch = resolveIPCondition(context.getClientIP(), condition.getValue());
                } catch (IPIsNotInValidFormException e) {
                    log.error(e.getName());
                    return false;
                }

                if (isIPMatch) {
                    return true;
                }

            }
        }
        return false;
    }

    private static boolean resolveIPCondition(String clientIP, String value) throws IPIsNotInValidFormException {
        try {
            long ipToTest = ipToLong(InetAddress.getByName(clientIP));

            String[] ipAddresses = value.split("-");

            if (ipAddresses.length > 2) {
                throw new IPIsNotInValidFormException(MessageFormat.format("IP address {0} is not in valid form!", value));
            }

            if (ipAddresses.length == 2) {
                long ipFrom = ipToLong(InetAddress.getByName(ipAddresses[0]));
                long ipTo = ipToLong(InetAddress.getByName(ipAddresses[1]));

                if (ipToTest >= ipFrom && ipToTest <= ipTo) {
                    return true;
                }
            }

            //one IP
            return ipToTest == ipToLong(InetAddress.getByName(ipAddresses[0]));
        } catch (UnknownHostException e) {
            throw new IPIsNotInValidFormException(MessageFormat.format("IP address {0} is not in valid form!", e.getMessage()));
        }
    }

    private static ConfigurationDTO getOrLoadConfiguration(String featureName) {
        ConfigurationDTO cachedConfiguration = featureFlagCachedMap.get(featureName);
        if (cachedConfiguration != null) {
            return cachedConfiguration;
        }

        ConfigurationDTO configurationDTO = FeatureFlagIntegrationService.getConfiguration(featureName);

        if (configurationDTO != null) {
            featureFlagCachedMap.put(featureName, configurationDTO);
        }

        return configurationDTO;
    }


    @Scheduled(fixedDelay = 20000)
    public void checkFeatures() {
        for (ConfigurationDTO configurationDTO : featureFlagCachedMap.values()) {

            ChecksumDTO checksumDTO = FeatureFlagIntegrationService.getCheckSum(configurationDTO.getName());

            if (checksumDTO == null) {
                log.info("Cant found checksum for " + configurationDTO.getName());
                continue;
            }

            if (!Arrays.equals(checksumDTO.getChecksum(), configurationDTO.getChecksum())) {
                ConfigurationDTO newConf = FeatureFlagIntegrationService.getConfiguration(configurationDTO.getName());

                if (newConf != null) {
                    newConf.setChecksum(checksumDTO.getChecksum());
                    featureFlagCachedMap.put(configurationDTO.getName(), newConf);
                }
            }
        }
    }


    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }
}
