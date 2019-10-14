package org.dummy.spotify.configs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

/**
 * Fields that are used to make a REST call to Spotify.
 *
 * <p>@NonEmpty fields are required fields.</p>
 */
@Component // to make a default bean
@Data // to create getter setter
@JsonIgnoreProperties(ignoreUnknown = true) // to ignore
@PropertySource("classpath:application.properties") // to read property values
public class SpotifyConnectionConfig {

    @Value("${spotify.client-id}") // TODO add relevant fields
    @NotEmpty
    private String clientId;

    @Value("${spotify.client-secret}") // TODO add relevant fields
    @NotEmpty
    private String clientSecret;

    @Setter(AccessLevel.NONE)
    private String authorizeUrl = "https://accounts.spotify.com/authorize/";

    @Setter(AccessLevel.NONE)
    private String authorizeResponseType = "code";

    @Setter(AccessLevel.NONE)
   // private String authorizationScope = "user-read-private user-read-email streaming app-remote-control playlist-read-collaborative user-modify-playback-state";
    private String authorizationScope = "user-read-private playlist-read-private playlist-modify-public user-library-read user-read-recently-played streaming app-remote-control";

    @Value("${spotify.authorization.redirectURL}")
    @NotEmpty
    private String spotifyAuthorizationRedirectURL;

    @Setter(AccessLevel.NONE)
    private String tokenUrl = "https://accounts.spotify.com/api/token";

    @Setter(AccessLevel.NONE)
    private String playerUrlRecentlyPlayed = "https://api.spotify.com/v1/me/player/recently-played";

    @Setter(AccessLevel.NONE)
    private String tracksUrl = "https://api.spotify.com/v1/tracks/";
}
