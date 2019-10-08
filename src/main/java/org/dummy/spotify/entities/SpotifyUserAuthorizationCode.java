package org.dummy.spotify.entities;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class SpotifyUserAuthorizationCode {
    private String username;
    private String code;
    private String accessToken;
    private String tokenType;
    private String refreshToken;
}
//TODO encrypt and persist this db