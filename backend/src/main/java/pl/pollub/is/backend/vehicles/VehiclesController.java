package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesController {
    private final VehiclesService vehiclesService;

    @GetMapping("/counts/by-area-code")
    public Object groupByAreaCode() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getVehiclesCountByAreaCode());
    }
}
