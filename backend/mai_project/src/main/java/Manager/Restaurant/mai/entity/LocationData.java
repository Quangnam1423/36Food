package Manager.Restaurant.mai.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationData {
    private Double latitude;
    private Double longitude;

    public LocationData(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}