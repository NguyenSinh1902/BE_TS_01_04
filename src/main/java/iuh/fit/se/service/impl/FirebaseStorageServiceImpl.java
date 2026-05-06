package iuh.fit.se.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import iuh.fit.se.service.FirebaseStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 1. Tạo tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 2. Lấy Bucket
        Bucket bucket = StorageClient.getInstance().bucket();

        // 3. Đẩy dữ liệu lên Firebase
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

        // 4. Trả về URL
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(), fileName);
    }
}