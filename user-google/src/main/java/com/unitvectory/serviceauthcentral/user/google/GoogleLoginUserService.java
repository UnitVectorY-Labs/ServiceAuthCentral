/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.serviceauthcentral.user.google;

import java.io.IOException;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unitvectory.serviceauthcentral.user.LoginUserService;
import com.unitvectory.serviceauthcentral.user.UserContext;
import com.unitvectory.serviceauthcentral.util.exception.BadRequestException;
import com.unitvectory.serviceauthcentral.util.exception.ConflictException;
import com.unitvectory.serviceauthcentral.util.exception.ForbiddenException;
import com.unitvectory.serviceauthcentral.util.exception.InternalServerErrorException;
import com.unitvectory.serviceauthcentral.util.exception.NotFoundException;
import com.unitvectory.serviceauthcentral.util.exception.UnauthorizedException;

import lombok.Data;

/**
 * The Google implementation of the Login Provider Service to facilitate logins
 * with Google
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
@Profile("!test")
public class GoogleLoginUserService implements LoginUserService {

    private static final String PROVIDER = "google";

    private static final String PROVIDER_NAME = "Google";

    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";

    private static final String USER_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";

    private static final String AUTHORIZE_SCHEME = "https";

    private static final String AUTHORIZE_HOST = "accounts.google.com";

    private static final String AUTHORIZE_PATH = "/o/oauth2/v2/auth";

    @Value("${sac.user.provider.google.clientid}")
    private String clientId;

    @Value("${sac.user.provider.google.clientsecret}")
    private String clientSecret;

    @Value("${sac.token.url}")
    private String tokenUrl;

    @Override
    public String getServiceDisplayName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean isActive() {
        return this.clientId != null && this.clientSecret != null;
    }

    @Override
    public String getClientId() {
        return "provider:" + PROVIDER;
    }

    @Override
    public String getAuthorizationRedirectUri(String state) {
        try {
            return new URIBuilder()
                    .setScheme(AUTHORIZE_SCHEME)
                    .setHost(AUTHORIZE_HOST)
                    .setPath(AUTHORIZE_PATH)
                    .addParameter("client_id", this.clientId)
                    .addParameter("state", state)
                    .addParameter("redirect_uri", this.tokenUrl + "/login/callback")
                    .addParameter("response_type", "code")
                    .addParameter("scope", "profile")
                    .build().toURL()
                    .toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserContext authorizationCodeToUserContext(String code) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            ObjectMapper objectMapper = new ObjectMapper();

            String accessToken = getAccessToken(client, objectMapper, code);
            UserDataResponse userData = getUserData(client, objectMapper, accessToken);

            return UserContext.builder().provider(PROVIDER).userId(String.valueOf(userData.getSub()))
                    .userName(userData.getName()).build();
        } catch (IOException e) {
            throw new InternalServerErrorException("Error during HTTP operations", e);
        }
    }

    private String getAccessToken(CloseableHttpClient client, ObjectMapper objectMapper,
            String code) throws IOException {
        HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT);
        String jsonBody = objectMapper.writeValueAsString(
                Map.of("client_id", clientId, "client_secret", clientSecret, "code", code, "grant_type",
                        "authorization_code", "redirect_uri", this.tokenUrl + "/login/callback"));

        StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        HttpClientResponseHandler<String> responseHandler = response -> {
            int statusCode = response.getCode();
            if (statusCode != 200) {
                handleErrorResponse(statusCode, "Failed to retrieve access token");
            }
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        };

        String json = client.execute(httpPost, responseHandler);
        AccessTokenResponse accessTokenResponse = objectMapper.readValue(json, AccessTokenResponse.class);
        return accessTokenResponse.getAccessToken();
    }

    private UserDataResponse getUserData(CloseableHttpClient client, ObjectMapper objectMapper,
            String accessToken) throws IOException {
        HttpGet httpGetUser = new HttpGet(USER_ENDPOINT + "?access_token=" + accessToken);

        HttpClientResponseHandler<UserDataResponse> responseHandler = response -> {
            int statusCode = response.getCode();
            if (statusCode != 200) {
                handleErrorResponse(statusCode, "Failed to retrieve user data");
            }
            HttpEntity responseEntity = response.getEntity();
            String userJson = EntityUtils.toString(responseEntity);
            return objectMapper.readValue(userJson, UserDataResponse.class);
        };

        return client.execute(httpGetUser, responseHandler);
    }

    private void handleErrorResponse(int statusCode, String message) {
        switch (statusCode) {
            case 400:
                throw new BadRequestException(message);
            case 401:
                throw new UnauthorizedException(message);
            case 403:
                throw new ForbiddenException(message);
            case 404:
                throw new NotFoundException(message);
            case 409:
                throw new ConflictException(message);
            default:
                throw new InternalServerErrorException(message);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AccessTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UserDataResponse {
        private String sub;
        private String name;
    }
}
