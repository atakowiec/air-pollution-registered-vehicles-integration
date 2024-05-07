package pl.pollub.is.backend.pollution;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/pollution")
@RequiredArgsConstructor
public class AirPollutionController {
    private final AirPollutionService airPollutionService;

    @PostMapping("/import")
    public Map<String, Integer> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        return airPollutionService.handleFileUpload(file);
    }
}
