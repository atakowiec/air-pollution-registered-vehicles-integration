package pl.pollub.is.backend.general;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
@RequiredArgsConstructor
public class GeneralController {
    private final GeneralService generalService;

    @GetMapping("/data/counts/by-year-and-voivodeships")
    public ResponseEntity<String> getDataByYearAndVoivodeships() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(generalService.getDataByYearAndVoivodeships());
    }

    @GetMapping("/export/counts/by-year-and-voivodeships/{format}")
    public ResponseEntity<byte[]> exportDataByYearAndVoivodeships(@PathVariable ExportFormat format,
                                                                  @RequestParam(required = false, defaultValue = "-1") int startYear,
                                                                  @RequestParam(required = false, defaultValue = "-1") int endYear,
                                                                  @RequestParam(required = false, defaultValue = "*") String voivodeships,
                                                                  @RequestParam(required = false, defaultValue = "*") String indicators) {
        ByteArrayOutputStream outputStream = switch (format) {
            case JSON -> generalService.exportDataAsJson(startYear, endYear, voivodeships, indicators);
            case CSV -> generalService.exportDataAsCsv(startYear, endYear, voivodeships, indicators);
            case XLSX -> generalService.exportDataAsXlsx(startYear, endYear, voivodeships, indicators);
            case YAML -> generalService.exportDataAsYaml(startYear, endYear, voivodeships, indicators);
            case XML -> generalService.exportDataAsXml(startYear, endYear, voivodeships, indicators);
        };

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", format.generateFileName());
        httpHeaders.add(HttpHeaders.CONTENT_ENCODING, "UTF-8");

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(outputStream.toByteArray());
    }
}
