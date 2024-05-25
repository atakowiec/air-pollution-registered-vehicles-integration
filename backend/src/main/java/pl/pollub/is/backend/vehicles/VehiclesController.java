package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehiclesService vehiclesService;
    private final VehiclesRepository vehiclesRepository;

    @PostMapping("/import/csv")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            return vehiclesService.processCsvFile(file);
        } catch (IOException | ParseException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/counts/by-area-code")
    public Object groupByAreaCode() {
        List<Object[]> dbResult = vehiclesRepository.countVehiclesByAreaCode();
        Map<Object, Object> result = new HashMap<>();
        for (Object[] objects : dbResult) {
            result.put(objects[0], objects[1]);
        }

        return result;
    }
}
