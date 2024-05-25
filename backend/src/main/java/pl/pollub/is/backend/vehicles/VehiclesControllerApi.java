package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehiclesControllerApi {

    private final VehiclesServiceApi vehiclesServiceApi;

    @PostMapping("/import/api")
    public ResponseEntity<Map<String, Integer>> fetchDataFromApi(@RequestBody String apiUrl) throws IOException {
        try {
            vehiclesServiceApi.processJsonFromUrl(apiUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}

