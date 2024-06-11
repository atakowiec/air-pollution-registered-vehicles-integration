package pl.pollub.is.backend.vehicles.file;

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

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesControllerFile {

    private final VehiclesServiceFile vehiclesService;

    @PostMapping("/import/csv")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".csv")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            return vehiclesService.processCsvFile(file);
        } catch (IOException | ParseException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
