package pl.pollub.is.backend.pollution;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pollution")
@RequiredArgsConstructor
public class AirPollutionController {
    private final AirPollutionService airPollutionService;

    @PostMapping("/import")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return airPollutionService.handleFileUpload(file);
    }
}
