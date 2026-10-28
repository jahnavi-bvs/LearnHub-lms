package com.learnhub.gateway;

import com.learnhub.gateway.config.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A tiny fake backend (the JDK's built-in HttpServer) stands in for course- and enrollment-service.
 * It echoes back the path and Authorization header it received, proving the gateway forwarded them.
 */
@SpringBootTest
@AutoConfigureMockMvc
class GatewayRoutingTest {

    private static HttpServer fakeBackend;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @BeforeAll
    static void startFakeBackend() throws IOException {
        fakeBackend = HttpServer.create(new InetSocketAddress("localhost", 0), 0); // 0 = any free port
        fakeBackend.createContext("/", exchange -> {
            String auth = exchange.getRequestHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String json = "{\"path\":\"" + exchange.getRequestURI().getPath() + "\",\"auth\":\""
                    + (auth == null ? "none" : auth.substring(0, 7)) + "\"}"; // just "Bearer "
            byte[] body = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        fakeBackend.start();
    }

    @AfterAll
    static void stopFakeBackend() {
        fakeBackend.stop(0);
    }

    /** Points the routes at the fake backend; assessment at a port where nothing listens. */
    @DynamicPropertySource
    static void routeToFakeBackend(DynamicPropertyRegistry registry) {
        registry.add("learnhub.services.course-url", () -> "http://localhost:" + fakeBackend.getAddress().getPort());
        registry.add("learnhub.services.enrollment-url", () -> "http://localhost:" + fakeBackend.getAddress().getPort());
        registry.add("learnhub.services.assessment-url", () -> "http://localhost:1");
    }

    private String token() {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject("3")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("role", "STUDENT")
                .build();
        return "Bearer " + new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    @Test
    void publicCatalog_isRoutedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/api/courses"))
                .andExpect(jsonPath("$.auth").value("none"));
    }

    @Test
    void protectedRoute_withoutToken_isRejectedAtTheEdge() throws Exception {
        mockMvc.perform(get("/api/enrollments/mine"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    void protectedRoute_withToken_isForwardedWithAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/enrollments/mine").header(HttpHeaders.AUTHORIZATION, token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("/api/enrollments/mine"))
                .andExpect(jsonPath("$.auth").value("Bearer "));
    }

    @Test
    void forgedToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/enrollments/mine").header(HttpHeaders.AUTHORIZATION, "Bearer not.a.jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void serviceDown_returns503() throws Exception {
        mockMvc.perform(get("/api/assessments/submissions/mine").header(HttpHeaders.AUTHORIZATION, token()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Service unavailable"));
    }

    @Test
    void corsPreflight_fromReactDevServer_isAllowed() throws Exception {
        mockMvc.perform(options("/api/enrollments")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    @Test
    void corsPreflight_fromUnknownOrigin_isRejected() throws Exception {
        mockMvc.perform(options("/api/enrollments")
                        .header(HttpHeaders.ORIGIN, "http://evil.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isForbidden());
    }
}
