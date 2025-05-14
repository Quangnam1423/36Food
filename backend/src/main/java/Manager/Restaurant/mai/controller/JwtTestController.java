package Manager.Restaurant.mai.controller;

import Manager.Restaurant.mai.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing JWT token generation and validation
 * This is purely for diagnostic purposes
 */
@RestController
@RequestMapping("/jwt-test")
@RequiredArgsConstructor
public class JwtTestController {

    private final JwtService jwtService;

    @GetMapping("/key-info")
    public ResponseEntity<?> getKeyInfo() {
        // Test to ensure the JWT secret key is properly configured
        try {
            // This will test if the key can be properly loaded
            String testToken = jwtService.generateToken("test@example.com", 1L, "USER");
            
            // Analyze token structure
            String[] parts = testToken.split("\\.");
            Map<String, Object> response = new HashMap<>();
            
            response.put("tokenValid", parts.length == 3);
            response.put("tokenLength", testToken.length());
            
            // Decode header and payload for inspection (don't do this in production)
            if (parts.length >= 2) {
                try {
                    String headerJson = new String(Base64.getDecoder().decode(parts[0]));
                    response.put("header", headerJson);
                } catch (Exception e) {
                    response.put("headerError", e.getMessage());
                }
                
                try {
                    String payloadJson = new String(Base64.getDecoder().decode(parts[1]));
                    response.put("payload", payloadJson);
                } catch (Exception e) {
                    response.put("payloadError", e.getMessage());
                }
            }
            
            // Test token validation
            boolean isValid = jwtService.isTokenValid(testToken);
            response.put("tokenValidation", isValid);
            
            if (isValid) {
                try {
                    Claims claims = jwtService.extractAllClaims(testToken);
                    response.put("extractedSubject", claims.getSubject());
                    response.put("extractedUserId", claims.get("userId"));
                    response.put("extractedRole", claims.get("role"));
                } catch (Exception e) {
                    response.put("claimsExtractionError", e.getMessage());
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to generate or validate token",
                "message", e.getMessage(),
                "stackTrace", e.getStackTrace()
            ));
        }
    }
}
