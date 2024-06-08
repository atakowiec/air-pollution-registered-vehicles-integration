package pl.pollub.is.backend.pollution;

import lombok.RequiredArgsConstructor;
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
        return airPollutionService.handleFileUpload(file);
    }
}
