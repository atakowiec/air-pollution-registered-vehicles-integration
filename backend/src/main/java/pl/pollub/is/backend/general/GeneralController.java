package pl.pollub.is.backend.general;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class GeneralController {
    private final GeneralService generalService;

    @GetMapping("/counts/by-year-and-voivodeships")
    public ResponseEntity<String> getDataByYearAndVoivodeships() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(generalService.getDataByYearAndVoivodeships());
    }
}
