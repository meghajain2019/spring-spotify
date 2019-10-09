package org.dummy.spotify.services;

import lombok.extern.slf4j.Slf4j;
import org.dummy.spotify.configs.SpotifyConnectionConfig;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.dummy.spotify.services.AuthorizationService.callAction;
import static org.dummy.spotify.services.utils.RestCallUtils.checkResponseCodeExpected;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Component
@Slf4j
public class PlayerService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SpotifyConnectionConfig spotifyConnectionConfig;


    /**
     * Get Current User's Recently Played Tracks
     *
     * @return
     */
    public JSONObject getRecentlyPlayed(String token) {
        // prepare create index url
        String createIndexUrl = spotifyConnectionConfig.getPlayerUrlRecentlyPlayed();

        // prepare request headers
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.set("Authorization", token);

        // hit request
        ResponseEntity<JSONObject> responseEntity = callAction(restTemplate, "getRecentlyPlayed", createIndexUrl, GET,
                new HttpEntity<>(null, requestHeaders), JSONObject.class, null);

        // check response
        checkResponseCodeExpected(responseEntity, Arrays.asList(NO_CONTENT, OK), "getRecentlyPlayed");

        return responseEntity.getBody();
    }
}