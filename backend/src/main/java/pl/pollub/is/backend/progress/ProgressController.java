package pl.pollub.is.backend.progress;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.progress.model.Progress;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {
    public final ProgressService progressService;

    @GetMapping("{token}")
    public ResponseEntity<String> getProgress(@PathVariable String token) throws JsonProcessingException {
        Progress progress = progressService.getProgress(token);
        if (progress == null) {
            throw new HttpException(404, "Progress not found");
        }

        return progress.toResponseEntity();
    }
}
