package Manager.Restaurant.mai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class DistanceService {

    @Value("${openroute.api.key}")
    private String API_KEY;
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    public RouteInfo getDistanceAndDuration(double startLng, double startLat, double endLng, double endLat) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("api_key", API_KEY)
                .queryParam("start", startLng + "," + startLat)
                .queryParam("end", endLng + "," + endLat)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            double distance = root.path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("distance").asDouble();

            double duration = root.path("features").get(0)
                    .path("properties")
                    .path("segments").get(0)
                    .path("duration").asDouble();

            return new RouteInfo(distance, duration);

        } catch (Exception e) {
            throw new RuntimeException("Failed to get route info", e);
        }
    }

    public static class RouteInfo {
        public double distanceInMeters;
        public double durationInSeconds;

        public RouteInfo(double distanceInMeters, double durationInSeconds) {
            this.distanceInMeters = distanceInMeters;
            this.durationInSeconds = durationInSeconds;
        }
    }
}

