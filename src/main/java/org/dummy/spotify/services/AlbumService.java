package org.dummy.spotify.services;

import org.dummy.spotify.configs.SpotifyConnectionConfig;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.dummy.spotify.services.AuthorizationService.callAction;
import static org.dummy.spotify.services.utils.RestCallUtils.checkResponseCodeExpected;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Service
public class AlbumService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SpotifyConnectionConfig spotifyConnectionConfig;


    /**
     * Get album by id
     *
     * @return
     */
    public JSONObject getById(String token, String id) {
        // prepare create index url
        String createIndexUrl = spotifyConnectionConfig.getAlbumUrl() + id;

        // prepare request headers
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.set("Authorization", token);

        // hit request
        ResponseEntity<JSONObject> responseEntity = callAction(restTemplate, "getAlbum", createIndexUrl, GET,
                new HttpEntity<>(null, requestHeaders), JSONObject.class, null);

        // check response
        checkResponseCodeExpected(responseEntity, Arrays.asList(NO_CONTENT, OK), "getAlbum");

        return responseEntity.getBody();
    }
}
