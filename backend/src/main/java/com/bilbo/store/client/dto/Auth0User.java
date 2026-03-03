package com.bilbo.store.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auth0User {

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("identities")
    private List<Identity> identities;

    @JsonProperty("name")
    private String name;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("last_password_reset")
    private String lastPasswordReset;

    @JsonProperty("last_ip")
    private String lastIp;

    @JsonProperty("last_login")
    private String lastLogin;

    @JsonProperty("logins_count")
    private int loginsCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Identity {
        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("provider")
        private String provider;

        @JsonProperty("connection")
        private String connection;

        @JsonProperty("isSocial")
        private boolean isSocial;
    }
}
