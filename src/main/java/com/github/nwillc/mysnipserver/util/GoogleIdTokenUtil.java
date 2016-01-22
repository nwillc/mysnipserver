package com.github.nwillc.mysnipserver.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

public final class GoogleIdTokenUtil {
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final String ISSUER = "accounts.google.com";

    private GoogleIdTokenUtil() {}

    static public Optional<Payload> verify(final String googleTokenId) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .setIssuer(ISSUER)
                .build();
        GoogleIdToken idToken = verifier.verify(googleTokenId);
        if (idToken != null) {
            return Optional.of(idToken.getPayload());
        }
        return Optional.empty();
    }
}
