package com.alten.business.services;

import com.alten.business.exceptions.KeycloakSecurityException;
import com.alten.controllers.dtos.SecurityDataInDto;
import com.alten.controllers.dtos.SecurityDataOutDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.security.KeyException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class AuthenticationService {


    private final WebClient keycloakService;
    private final String realm;
    private final String clientId;
    private final String clientSecret;
    private final String adminId;
    private final String adminSecret;

    public AuthenticationService(
            WebClient.Builder webClientBuilder,
            @Value("${spring.security.oauth2.auth-server-url}") String authServerUrl,
            @Value("${spring.security.oauth2.realm}") String realm,
            @Value("${spring.security.oauth2.client-id}") String clientId,
            @Value("${spring.security.oauth2.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.admin-id}") String adminId,
            @Value("${spring.security.oauth2.admin-secret}") String adminSecret
    ) {
        this.keycloakService = webClientBuilder.baseUrl(authServerUrl).build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.adminId = adminId;
        this.adminSecret = adminSecret;
    }

    /**
     * Authenticate against /protocol/openid-connect/token using password grant.
     */
    public SecurityDataOutDto authenticate(SecurityDataInDto in) {
        return keycloakService.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", in.getUsername())
                        .with("password", in.getPassword())
                )
                .retrieve()
                .bodyToMono(SecurityDataOutDto.class)
                .block();
    }

    /**
     * Register a new user via the Admin API, then authenticate them.
     */
    public void register(SecurityDataInDto in) throws KeycloakSecurityException {

        try {
            //1) authenticate with admin scope
            String adminToken = Objects.requireNonNull(keycloakService.post()
                            .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(BodyInserters
                                    .fromFormData("grant_type", "client_credentials")
                                    .with("client_id", adminId)
                                    .with("client_secret", adminSecret)
                            )
                            .retrieve()
                            .bodyToMono(SecurityDataOutDto.class)
                            .block())
                    .getAccessToken();

            // 2. Create user payload
            Map<String, Object> userPayload = Map.of(
                    "username", in.getUsername(),
                    "enabled", true,
                    "email", in.getEmail(),
                    "emailVerified", true,
                    "firstName", in.getFirstName(),
                    "lastName", in.getLastName(),
                    "credentials", List.of(
                            Map.of(
                                    "type", "password",
                                    "value", in.getPassword(),
                                    "temporary", false
                            )
                    )
            );

            // 3. Call Admin API to create the user
            var createResponse = keycloakService.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userPayload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            // 4) Extract the new userId from the Location header
            assert createResponse != null;
            URI location = createResponse.getHeaders().getLocation();
            assert location != null;
            String userId = location.getPath().substring(location.getPath().lastIndexOf('/') + 1);

            // 5) Fetch the RoleRepresentation for the role you want to assign
            Object roleRep = keycloakService.get()
                    .uri("/admin/realms/{realm}/roles/{role}", realm, in.getRole())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            // 6) Assign that realm role to the user
            assert roleRep != null;
            keycloakService.post()
                    .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(List.of(roleRep))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Unable to register new user.", e);
            throw new KeycloakSecurityException(e.getMessage());
        }


    }
}
