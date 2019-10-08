package org.dummy.spotify.services.utils;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class RestCallUtils {

    public static void checkRequestParameterNotNull(String parameterName, Object parameter) {
        if (parameter == null) {
            log.error("request parameter:[{}] must not be null.", parameterName);
            throw new IllegalArgumentException("request parameter:[" + parameterName + "] must not be null");
        }
    }

    public static void checkResponseCodeExpected(ResponseEntity<JSONObject> responseEntity, List<HttpStatus> expectedCodes, String message) {
        if (!expectedCodes.contains(responseEntity.getStatusCode())) {
            String errorMessage = responseEntity.getBody().toJSONString();
            log.error("Response code received was not expected. Method: {}.\n {}", message, errorMessage);
            throw new RuntimeException("Response code received was not expected. Method: " + message + "\n" + errorMessage);
        }
    }

    public static void checkResponseCodeExpectedString(ResponseEntity<String> responseEntity, List<HttpStatus> expectedCodes, String message) {
        if (!expectedCodes.contains(responseEntity.getStatusCode())) {
            String errorMessage = responseEntity.getBody();
            log.error("Response code received was not expected. Method: {}.\n {}", message, errorMessage);
            throw new RuntimeException("Response code received was not expected. Method: " + message + "\n" + errorMessage);
        }
    }
}
