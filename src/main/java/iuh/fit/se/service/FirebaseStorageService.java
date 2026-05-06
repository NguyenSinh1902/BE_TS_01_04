package iuh.fit.se.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FirebaseStorageService {
    String uploadFile(MultipartFile file) throws IOException;
}