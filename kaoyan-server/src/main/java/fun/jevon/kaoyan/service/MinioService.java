package fun.jevon.kaoyan.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    @SneakyThrows
    public List<String> listTree() {
        Set<String> dirs = new TreeSet<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).recursive(true).build());
        for (Result<Item> r : results) {
            String name = r.get().objectName();
            if (name.contains("/")) {
                String[] parts = name.split("/");
                StringBuilder path = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    path.append(parts[i]).append("/");
                    dirs.add(path.toString());
                }
            }
        }
        return new ArrayList<>(dirs);
    }

    @SneakyThrows
    public List<FileInfo> listFiles(String prefix) {
        List<FileInfo> files = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .recursive(false)
                        .build());
        for (Result<Item> r : results) {
            Item item = r.get();
            String name = item.objectName();
            if (name.equals(prefix)) continue;
            if (!name.endsWith("/")) {
                files.add(new FileInfo(name, item.size(), publicUrl(name)));
            }
        }
        return files;
    }

    @SneakyThrows
    public List<String> listDirs(String prefix) {
        Set<String> dirs = new TreeSet<>();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .recursive(true)
                        .build());
        for (Result<Item> r : results) {
            String name = r.get().objectName();
            if (name.equals(prefix)) continue;
            if (prefix.isEmpty()) {
                int idx = name.indexOf('/');
                if (idx > 0) {
                    dirs.add(name.substring(0, idx + 1));
                }
            } else {
                String relative = name.substring(prefix.length());
                int idx = relative.indexOf('/');
                if (idx > 0) {
                    dirs.add(prefix + relative.substring(0, idx + 1));
                }
            }
        }
        return new ArrayList<>(dirs);
    }

    @SneakyThrows
    public String upload(MultipartFile file, String prefix) {
        String original = file.getOriginalFilename();
        String objectName = prefix + UUID.randomUUID().toString().replace("-", "") + "_" + original;
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
        return publicUrl(objectName);
    }

    @SneakyThrows
    public void mkdir(String prefix) {
        String dir = prefix.endsWith("/") ? prefix : prefix + "/";
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(dir)
                        .stream(new java.io.ByteArrayInputStream(new byte[0]), 0, -1)
                        .contentType("application/x-directory")
                        .build());
    }

    @SneakyThrows
    public void delete(String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());
    }

    @SneakyThrows
    public InputStream getObject(String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());
    }

    public String publicUrl(String objectName) {
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucket + "/" + objectName;
    }

    public record FileInfo(String objectName, long size, String url) {
    }
}
