package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehiclesService vehiclesService;

    @PostMapping("/import/csv")
    public ResponseEntity<Map<String, Integer>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            vehiclesService.processCsvFile(file);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException | ParseException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
