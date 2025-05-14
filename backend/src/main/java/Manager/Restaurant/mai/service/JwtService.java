package Manager.Restaurant.mai.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class JwtService {
    private static final Logger logger = Logger.getLogger(JwtService.class.getName());
    
    @Value("${jwt.secret:defaultSecretKeyThatIsAtLeast32CharactersLong}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;    public String generateToken(String userEmail, Long userId, String role) {
        try {
            if (userEmail == null) {
                logger.warning("JWT generation failed: userEmail is null");
                throw new IllegalArgumentException("User email cannot be null");
            }
            if (userId == null) {
                logger.warning("JWT generation failed: userId is null");
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userId);
            claims.put("role", role);

            logger.info("Starting JWT token generation for user: " + userEmail);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userEmail)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            
            logger.info("Token generation completed: " + (token != null ? "Token length: " + token.length() : "null"));
            
            // Simple validation to ensure the token looks like a JWT (three parts separated by dots)
            if (token != null && !token.isEmpty()) {
                String[] parts = token.split("\\.");
                if (parts.length != 3) {
                    logger.severe("Generated token does not appear to be a valid JWT (should have 3 parts): " + parts.length);
                } else {
                    logger.info("JWT format validation successful: header.payload.signature structure confirmed");
                }
            }
            
            return token;
        } catch (Exception e) {
            logger.severe("Error generating JWT token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not generate token", e);
        }
    }    private Key getSigningKey() {
        try {
            // Log key information (don't log the full key in production)
            logger.info("Secret key length: " + secretKey.length());
            
            // Convert to byte array using UTF-8 encoding
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            
            // Check if key length meets minimum requirement for HMAC-SHA256 (256 bits = 32 bytes)
            if (keyBytes.length < 32) {
                logger.severe("Secret key is too short! Length: " + keyBytes.length + 
                             " bytes. Minimum required: 32 bytes (256 bits)");
                // Pad or hash the key if too short (for testing only, prefer using a proper key)
                byte[] paddedKey = new byte[32];
                System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
                logger.warning("Key was padded to 32 bytes for testing. Please use a proper key in production!");
                keyBytes = paddedKey;
            }
            
            logger.info("Key length in bits: " + (keyBytes.length * 8) + 
                " | Key bytes length: " + keyBytes.length);
            
            // Use HMAC-SHA key generation
            Key key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("Signing key generated with algorithm: " + key.getAlgorithm());
            
            return key;
        } catch (Exception e) {
            logger.severe("Error creating signing key: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create signing key", e);
        }
    }public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            boolean isValid = !expiration.before(new Date());
            logger.info("Token validation result: " + isValid);
            return isValid;
        } catch (Exception e) {
            logger.warning("Token validation failed: " + e.getMessage());
            return false;
        }
    }
}
