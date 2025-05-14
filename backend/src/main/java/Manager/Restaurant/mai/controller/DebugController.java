package Manager.Restaurant.mai.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

/**
 * Controller for debugging token issues
 */
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final Manager.Restaurant.mai.service.JwtService jwtService;

    @PostMapping("/decode-token")
    public ResponseEntity<?> decodeToken(@RequestBody TokenRequest request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return ResponseEntity.badRequest().body("Token is required");
            }
            
            String token = request.getToken();
            String[] parts = token.split("\\.");
            
            if (parts.length != 3) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid token format",
                    "expectedParts", 3,
                    "actualParts", parts.length
                ));
            }
            
            String header = "";
            String payload = "";
            
            try {
                header = new String(Base64.getUrlDecoder().decode(parts[0]));
            } catch (Exception e) {
                header = "Error decoding header: " + e.getMessage();
            }
            
            try {
                payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            } catch (Exception e) {
                payload = "Error decoding payload: " + e.getMessage();
            }
            
            return ResponseEntity.ok(Map.of(
                "header", header,
                "payload", payload,
                "signature", parts[2].substring(0, Math.min(10, parts[2].length())) + "..."
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to decode token",
                "message", e.getMessage()
            ));
        }
    }
    
    @Data
    @AllArgsConstructor
    static class TokenRequest {
        private String token;
    }
}
