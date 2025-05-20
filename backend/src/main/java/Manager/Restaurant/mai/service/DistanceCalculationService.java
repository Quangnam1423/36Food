package Manager.Restaurant.mai.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating distance between two coordinates using the Haversine formula
 * and calculating delivery fees based on the distance.
 */
@Service
public class DistanceCalculationService {

    private static final double EARTH_RADIUS_KM = 6371.0; // Bán kính trái đất theo km
    private static final BigDecimal BASE_DELIVERY_FEE = new BigDecimal("10000"); // Phí giao cơ bản cho 1km đầu
    private static final BigDecimal ADDITIONAL_KM_FEE = new BigDecimal("5000"); // Phí cho mỗi km tiếp theo
    private static final double BASE_DISTANCE_KM = 1.0; // Khoảng cách cơ bản (1km)

    /**
     * Calculate distance between two points using Haversine formula
     * @param startLat Starting point latitude
     * @param startLng Starting point longitude
     * @param endLat Ending point latitude
     * @param endLng Ending point longitude
     * @return Distance in kilometers
     */
    public double calculateDistanceInKm(double startLat, double startLng, double endLat, double endLng) {
        // Convert to radians
        double startLatRad = Math.toRadians(startLat);
        double startLngRad = Math.toRadians(startLng);
        double endLatRad = Math.toRadians(endLat);
        double endLngRad = Math.toRadians(endLng);

        // Calculate differences
        double latDiff = endLatRad - startLatRad;
        double lngDiff = endLngRad - startLngRad;

        // Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(startLatRad) * Math.cos(endLatRad) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate delivery fee based on the distance
     * First 1km: 10,000 VND
     * Each additional km: 5,000 VND per km
     *
     * @param distanceKm Distance in kilometers
     * @return Delivery fee in VND
     */
    public BigDecimal calculateDeliveryFee(double distanceKm) {
        BigDecimal fee = BASE_DELIVERY_FEE;
        
        if (distanceKm <= BASE_DISTANCE_KM) {
            return fee;
        }
        
        // Calculate additional fee for distance beyond the base 1km
        double additionalDistance = distanceKm - BASE_DISTANCE_KM;
        if (additionalDistance > 0) {
            // Round up to nearest 0.1 km
            double roundedAdditionalDist = Math.ceil(additionalDistance * 10) / 10;
            BigDecimal additionalFee = ADDITIONAL_KM_FEE.multiply(
                    BigDecimal.valueOf(roundedAdditionalDist)
            );
            fee = fee.add(additionalFee);
        }
        
        return fee.setScale(0, RoundingMode.CEILING);
    }
    
    /**
     * Calculate delivery fee based on start and end coordinates
     *
     * @param startLat Start latitude
     * @param startLng Start longitude
     * @param endLat End latitude
     * @param endLng End longitude
     * @return Delivery fee in VND
     */
    public BigDecimal calculateDeliveryFee(double startLat, double startLng, double endLat, double endLng) {
        double distanceKm = calculateDistanceInKm(startLat, startLng, endLat, endLng);
        return calculateDeliveryFee(distanceKm);
    }
    
    /**
     * Estimate delivery time based on distance and average speed of 30 km/h
     *
     * @param distanceKm Distance in kilometers
     * @return Estimated delivery time in minutes
     */
    public int estimateDeliveryTimeMinutes(double distanceKm) {
        // Assume average speed of 30 km/h (0.5 km/min)
        double timeInMinutes = distanceKm / 0.5;
        
        // Add fixed 10 minutes for restaurant preparation
        timeInMinutes += 10;
        
        // Return rounded up to nearest minute
        return (int) Math.ceil(timeInMinutes);
    }
}
