package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesController {
    private final VehiclesService vehiclesService;

    @GetMapping
    public ResponseEntity<Page<Vehicle>> getVehicles(
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer manufactureYear,
            @RequestParam(required = false) String fuelType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getVehicles(areaCode, brand, model, manufactureYear, fuelType, pageable));
    }

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

    @GetMapping("/counts/registrations-by-year-and-voivodeships")
    public Object getRegistrationsByAreaCodeAndVoivodeships() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getRegistrationsByAreaCodeAndVoivodeships());
    }

    @GetMapping("/counts/deregistrations-by-year-and-voivodeships")
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

    @GetMapping("/counts/by-fuel-type")
    public Object getVehiclesByFuelType() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getFuelTypes());
    }

    @GetMapping("/counts/top-10-brands")
    public Object getTop10Brands() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(vehiclesService.getTop10Brands());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehiclesService.getVehicleById(id);
        if (vehicle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehiclesService.createVehicle(vehicle));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Vehicle updatedVehicle = vehiclesService.updateVehicle(id, vehicle);
        if (updatedVehicle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        if (vehiclesService.deleteVehicle(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
