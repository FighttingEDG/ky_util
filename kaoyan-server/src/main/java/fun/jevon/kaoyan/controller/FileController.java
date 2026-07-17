package fun.jevon.kaoyan.controller;

import fun.jevon.kaoyan.dto.ApiResponse;
import fun.jevon.kaoyan.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final MinioService minioService;

    @GetMapping("/tree")
    public ApiResponse<List<String>> tree() {
        return ApiResponse.success(minioService.listTree());
    }

    @GetMapping("/list")
    public ApiResponse<List<MinioService.FileInfo>> list(
            @RequestParam(defaultValue = "") String prefix) {
        return ApiResponse.success(minioService.listFiles(prefix));
    }

    @GetMapping("/dirs")
    public ApiResponse<List<String>> dirs(
            @RequestParam(defaultValue = "") String prefix) {
        return ApiResponse.success(minioService.listDirs(prefix));
    }

    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "") String prefix) {
        String url = minioService.upload(file, prefix);
        return ApiResponse.success(url);
    }

    @PostMapping("/mkdir")
    public ApiResponse<Void> mkdir(@RequestBody Map<String, String> body) {
        minioService.mkdir(body.get("prefix"));
        return ApiResponse.success();
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam String objectName) {
        minioService.delete(objectName);
        return ApiResponse.success();
    }
}
