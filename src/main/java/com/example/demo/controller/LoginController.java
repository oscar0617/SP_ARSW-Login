package com.example.demo.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.example.demo.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/correct")
    public RedirectView getToken(
        @RegisteredOAuth2AuthorizedClient("aad") OAuth2AuthorizedClient authorizedClient,
        HttpServletResponse response,
        RedirectAttributes redirectAttributes) throws IOException, InterruptedException {
        String token = authorizedClient.getAccessToken().getTokenValue();
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://graph.microsoft.com/v1.0/me"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> responseGraph = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseGraph.body());
        String displayName = jsonNode.path("displayName").asText();
        String userPrincipalName = jsonNode.path("userPrincipalName").asText();
        System.out.println("Display Name: " + displayName);
        System.out.println("User Principal Name: " + userPrincipalName);
        if(userPrincipalName.length() == 0){
            userPrincipalName = loginService.getEmailFromToken(token);
            String[] data = userPrincipalName.split("@");
            displayName = data[0];
        }    
        String redirectUrl = String.format(
            "http://localhost:3000/salas?displayName=%s&userPrincipalName=%s&id=%s",
            URLEncoder.encode(displayName, StandardCharsets.UTF_8),
            URLEncoder.encode(userPrincipalName, StandardCharsets.UTF_8),
            URLEncoder.encode(displayName, StandardCharsets.UTF_8)
        );
        System.out.println("Principal name:" + userPrincipalName + userPrincipalName.length());
        loginService.addPlayerSesion(userPrincipalName);
        return new RedirectView(redirectUrl);
    }

}