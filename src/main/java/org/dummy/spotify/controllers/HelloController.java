package org.dummy.spotify.controllers;


import org.dummy.spotify.entities.SpotifyUserAuthorizationCode;
import org.dummy.spotify.services.ArtistService;
import org.dummy.spotify.services.AuthorizationService;
import org.dummy.spotify.services.PlayerService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.dummy.spotify.services.utils.RestCallUtils.checkRequestParameterNotNull;

@RestController
@RequestMapping("/v1/")
public class HelloController {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    PlayerService playerService;

    @Autowired
    ArtistService artistService;

    SpotifyUserAuthorizationCode spotifyUserAuthorizationCode = new SpotifyUserAuthorizationCode();

    String authCode;

    @GetMapping("/authorize")
    public String authorize() {
        return authorizationService.grantApplicationAccess();
    }

    @GetMapping("/token")
    public JSONObject getToken() {
        JSONObject response = new JSONObject();
        if (spotifyUserAuthorizationCode.getCode() == null || spotifyUserAuthorizationCode.getCode().isEmpty()) {
            response.put("Error", "Application not authorized yet on user's behalf to access his data");
            return response;
        }
        JSONObject result = authorizationService.getToken(spotifyUserAuthorizationCode.getCode());
        spotifyUserAuthorizationCode.setAccessToken((String) result.get("access_token"));
        spotifyUserAuthorizationCode.setRefreshToken((String) result.get("refresh_token"));
        spotifyUserAuthorizationCode.setTokenType((String) result.get("token_type"));
        result.put("goToRecentlyPlayedLink", "http://localhost:8080/v1/recentlyPlayed");
        return result;
    }

    @GetMapping("/recentlyPlayed")
    public JSONObject getRecentPlayedTracks() {
        JSONObject response = new JSONObject();
        if (spotifyUserAuthorizationCode.getAccessToken() == null || spotifyUserAuthorizationCode.getAccessToken().isEmpty()) {
            response.put("Error", "UserAccessToken not fetched yet");
            return response;
        }
        JSONObject result = playerService.getRecentlyPlayed(spotifyUserAuthorizationCode.getTokenType() + " " + spotifyUserAuthorizationCode.getAccessToken());
        return result;
    }

    @GetMapping("/artist/{id}")
    public JSONObject getRecentPlayedTracks(@PathVariable String id) {
        JSONObject response = new JSONObject();
        if (spotifyUserAuthorizationCode.getAccessToken() == null || spotifyUserAuthorizationCode.getAccessToken().isEmpty()) {
            response.put("Error", "UserAccessToken not fetched yet");
            return response;
        }
        JSONObject result = artistService.getById(spotifyUserAuthorizationCode.getTokenType() + " " + spotifyUserAuthorizationCode.getAccessToken(), id);
        return result;
    }

    @RequestMapping("/responseFromSpotify")
    public String authResponse(@RequestParam(required = false) String code, @RequestParam(required = false) String state, @RequestParam(required = false) String error) {
        if (error != null) {
            return "<h1>AccessGranted</h1> <br/>  state:" + state + "  error: " + error;
        }
        spotifyUserAuthorizationCode.setCode(code);
        spotifyUserAuthorizationCode.setUsername("user-" + Thread.currentThread().getName());
        return "<h1>AccessGranted</h1> <br/>  state:" + state + " <br/>  code:" + code + "<br/> <br/> getToken <a href=\"http://localhost:8080/v1/token\">Get Token to interact on user's behalf</a>\n "; // TODO hide code display
    }

    @GetMapping("/{name}")
    public String hello(Model model, @PathVariable String name) {
        checkRequestParameterNotNull("username", name);
        return "Hello World! " + name;
    }
}
