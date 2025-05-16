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
            // Sử dụng công thức Haversine để tính khoảng cách khi API bị lỗi
            System.out.println("OpenRouteService API error: " + e.getMessage() + ". Using Haversine formula instead.");
            
            final double EARTH_RADIUS = 6371; // Bán kính của trái đất (km)
            
            // Chuyển đổi sang radian
            double dLat = Math.toRadians(endLat - startLat);
            double dLon = Math.toRadians(endLng - startLng);
            
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
            
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distanceInKm = EARTH_RADIUS * c;
            double distanceInMeters = distanceInKm * 1000;
            
            // Thời gian ước tính (giả định trung bình 30km/h)
            double durationInSeconds = (distanceInKm / 30) * 3600;
            
            return new RouteInfo(distanceInMeters, durationInSeconds);
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

