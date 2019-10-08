package org.dummy.spotify.services;

import lombok.extern.slf4j.Slf4j;
import org.dummy.spotify.configs.SpotifyConnectionConfig;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.dummy.spotify.services.utils.RestCallUtils.checkResponseCodeExpected;
import static org.dummy.spotify.services.utils.RestCallUtils.checkResponseCodeExpectedString;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Component
@Slf4j
public class AuthorizationService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SpotifyConnectionConfig spotifyConnectionConfig;

    /**
     * Let UI handle this operation.   as logically there should be a redirect to the thridparty website to make the login. instead of having thirdparty snippet in our app.
     * Let ui handle this redirect.
     *
     * @return
     */
    public String grantApplicationAccess() {
        // prepare create index url
        String createIndexUrl = spotifyConnectionConfig.getAuthorizeUrl();

        // set query parameters
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("scope", spotifyConnectionConfig.getAuthorizationScope());
        queryParams.put("response_type", spotifyConnectionConfig.getAuthorizeResponseType());
        queryParams.put("redirect_uri", spotifyConnectionConfig.getSpotifyAuthorizationRedirectURL());
        queryParams.put("client_id", spotifyConnectionConfig.getClientId());

        // prepare request headers
        HttpHeaders requestHeaders = new HttpHeaders();
        // requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        // hit request
        ResponseEntity<String> responseEntity = callAction(restTemplate, "authorize", createIndexUrl, GET,
                new HttpEntity<>(null, requestHeaders), String.class, queryParams);

        // check response
        checkResponseCodeExpectedString(responseEntity, Arrays.asList(CREATED, OK), "authorize");

        return responseEntity.getBody();
    }

    /**
     * Get token for user who has authorized us to interact with spotify to fetch data on his behalf
     *
     * @param code The authorization code returned from the initial request to the Account /authorize endpoint.
     * @return
     */
    public JSONObject getToken(String code) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        // prepare create index url
        String createIndexUrl = spotifyConnectionConfig.getTokenUrl();

        // prepare request headers
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Content-Type", APPLICATION_FORM_URLENCODED_VALUE);
        requestHeaders.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                (spotifyConnectionConfig.getClientId() + ":" + spotifyConnectionConfig.getClientSecret()).getBytes()
        ));

        // set body
        requestBody.put("grant_type", Arrays.asList("authorization_code"));
        requestBody.put("code", Arrays.asList(code));
        requestBody.put("redirect_uri", Arrays.asList(spotifyConnectionConfig.getSpotifyAuthorizationRedirectURL()));

        // hit request
        ResponseEntity<JSONObject> responseEntity = callAction(restTemplate, "getToken", createIndexUrl, POST,
                new HttpEntity<>(requestBody, requestHeaders), JSONObject.class, null);

        // check response
        checkResponseCodeExpected(responseEntity, Arrays.asList(NO_CONTENT, OK), "getToken");

        return responseEntity.getBody();
    }

    protected static <T> ResponseEntity<T> callAction(RestTemplate restTemplate, String message, String url, HttpMethod method, HttpEntity entity, Class<T> responseClass, Map<String, String> queryParameters) {
        ResponseEntity<T> responseEntity;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
            if (queryParameters != null) {
                queryParameters.keySet().forEach(key -> builder.queryParam(key, queryParameters.get(key)));
            }
            responseEntity = restTemplate.exchange(builder.build(false).toUri(), method, entity, responseClass);
        } catch (RestClientResponseException e) {
            log.error("Error while making the " + message + " rest call. Message:{}", e.getResponseBodyAsString());
            throw new RuntimeException("Error while making the " + message + " rest call. Message:" + e.getResponseBodyAsString());
        }
        return responseEntity;
    }


}
