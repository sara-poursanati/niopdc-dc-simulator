package ir.niopdc.policy.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@UtilityClass
public class FileUtils {

    public static void deleteFile(String filePath) {
        Path path = Path.of(filePath);

        if (Files.notExists(path)) {
            log.warn("File deletion skipped; file not found at path: {}", filePath);
            return;
        }

        try {
            Files.delete(path);
            log.info("Successfully deleted file at path: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file at path: {}. Reason: {}", filePath, e.getMessage(), e);
        }
    }

    public static void createHash(String filePath){
         //Implement SHA-512 hash generation for the specified file.
    }
}
