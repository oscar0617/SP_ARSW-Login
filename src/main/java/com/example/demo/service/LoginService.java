package com.example.demo.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

/**
 * Login Service
 */
@Service
public class LoginService{
    
    /**
     * Gets the nickname of the authenticaded player
     * @param email email of the player
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private String getNickName(String email) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(email);
        String url = "http://10.0.0.5:8080/player/v1/nickname/" + email;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Sends the session of an authenticated player
     * @param nickName player's nickname authenticated
     * @throws IOException
     * @throws InterruptedException
     */
    private void sendPlayerSesion(String nickName) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        String url = "http://10.0.0.5:8080/player/v1/sesion/" + nickName;
        System.out.println(url);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

    /**
     * Saves the player in session storage
     * @param email email of the player
     * @throws IOException
     * @throws InterruptedException
     */
    public void addPlayerSesion(String email) throws IOException, InterruptedException{
        String nickName = getNickName(email);
        sendPlayerSesion(nickName);
    }

    /**
     * Returns the email of an authenticated player
     * @param token authenticated token
     * @return
     */
    public String getEmailFromToken(String token) {
        try {
            SignedJWT signedJWT = (SignedJWT) JWTParser.parse(token);
            return signedJWT.getJWTClaimsSet().getStringClaim("email");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}