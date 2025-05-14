package Manager.Restaurant.mai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Manager.Restaurant.mai.service.DistanceService;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    private final DistanceService distanceService;

    public RouteController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @GetMapping
    public DistanceService.RouteInfo getRouteInfo(
            @RequestParam double startLng,
            @RequestParam double startLat,
            @RequestParam double endLng,
            @RequestParam double endLat) {

        return distanceService.getDistanceAndDuration(startLng, startLat, endLng, endLat);
    }
}
