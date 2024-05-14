package pl.pollub.is.backend.progress;

import org.springframework.stereotype.Service;
import pl.pollub.is.backend.progress.model.Progress;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProgressService {
    private final Map<String, Progress> activeProgresses = new HashMap<>();

    public <T extends Progress> T registerProgress(T progress) {
        activeProgresses.put(progress.getKey(), progress);

        return progress;
    }

    public Progress getProgress(String token) {
        return activeProgresses.get(token);
    }

    public void removeProgress(String token) {
        activeProgresses.remove(token);
    }
}
