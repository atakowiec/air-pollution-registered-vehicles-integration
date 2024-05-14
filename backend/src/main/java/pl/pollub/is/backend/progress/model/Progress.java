package pl.pollub.is.backend.progress.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.pollub.is.backend.util.SimpleJsonBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Progress {
    private final String key;
    private ProgressStatus status = ProgressStatus.NOT_STARTED;
    private final Map<String, Object> progressData = new HashMap<>();

    public Progress(String key) {
        this.key = key;

        this.clear();
    }

    public String toJson() throws JsonProcessingException {
        return SimpleJsonBuilder.of("key", key)
                .add("status", status)
                .add("data", progressData)
                .toJson();
    }

    public ResponseEntity<String> toResponseEntity() throws JsonProcessingException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(toJson());
    }

    public void setProgressData(String key, Object value) {
        progressData.put(key, value);
    }

    public void removeProgressData(String key) {
        progressData.remove(key);
    }

    public void clear() {
        status = ProgressStatus.NOT_STARTED;
        progressData.clear();
    }
}
