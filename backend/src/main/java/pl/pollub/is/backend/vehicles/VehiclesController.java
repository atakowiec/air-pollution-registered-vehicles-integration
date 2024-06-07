package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.List;

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

    @GetMapping("/counts/by-area-code-and-year")
    public ResponseEntity<List<Vehicle>> getVehiclesByAreaCodeAndYear(
            @RequestParam String areaCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy") int year) {
        List<Vehicle> vehicles = vehiclesService.getVehiclesByAreaCodeAndRegistrationYear(areaCode, year);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/counts/by-year")
    public ResponseEntity<List<Object[]>> getVehiclesByYear(
            @RequestParam @DateTimeFormat(pattern = "yyyy") int year) {
        List<Object[]> vehiclesCountByYear = vehiclesService.getVehiclesCountByYear(year);
        return ResponseEntity.ok(vehiclesCountByYear);
    }

    @GetMapping("/counts/registrations-by-area-code-and-voivodeships")
    public Object getRegistrationsByAreaCodeAndVoivodeships() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getRegistrationsByAreaCodeAndVoivodeships());
    }

    @GetMapping("/counts/deregistrations-by-area-code-and-voivodeships")
    public Object getDeregistrationsByAreaCodeAndVoivodeships() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getDeregistrationsByAreaCodeAndVoivodeships());
    }

    @GetMapping("/counts/deregistrations-by-area-code")
    public Object getDeregistrationsByAreaCode() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getDeregistrationsByAreaCode());
    }
}
