package pl.pollub.is.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class FileUtil {
    public static File multipartToFile(MultipartFile file) throws IOException {
        if(file == null || file.getOriginalFilename() == null)
            return null;

        File convFile = Files.createTempFile("is_backend", UUID.randomUUID().toString()).toFile();
        file.transferTo(convFile);
        return convFile;
    }
}
