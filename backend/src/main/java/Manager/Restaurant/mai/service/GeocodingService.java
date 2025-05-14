package Manager.Restaurant.mai.service;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Pattern;

@Service
public class GeocodingService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\s*\\d{5,6}\\s*,?\\s*");

    public GeocodingService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String getAddressFromCoordinates(double lat, double lon) {
        String url = UriComponentsBuilder.newInstance()
                .uri(URI.create(NOMINATIM_URL))
                .queryParam("format", "json")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("accept-language", "vi")
                .build()
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            String fullAddress = root.path("display_name").asText();
            
            return removePostalCode(fullAddress);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get address from coordinates", e);
        }
    }

        private String removePostalCode(String address) {
        // Xóa mã bưu chính (VD: 70000 hoặc 700000)
        return POSTAL_CODE_PATTERN.matcher(address).replaceAll("");
    }
}