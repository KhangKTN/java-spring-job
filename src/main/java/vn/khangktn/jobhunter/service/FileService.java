package vn.khangktn.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Value("${upload-file.base-uri}") 
    private String baseURI;

    public void createFolder(String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if(!tmpDir.isDirectory()){
            try {
                Files.createDirectories(tmpDir.toPath());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        // create filename by format: fileName + "_" + currentTime (ms)
        String[] fileNameSplit = file.getOriginalFilename().split("[.]");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String finalName = file.getOriginalFilename().substring(0, file.getOriginalFilename().length() - extension.length() - 1) 
            + "_" 
            + System.currentTimeMillis()
            + "." + extension;

        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        System.out.println(fileName);
        System.out.println(folder);
        System.out.println(uri.toString());

        // file không tồn tại, hoặc file là 1 director => return 0
        if (!tmpDir.exists() || tmpDir.isDirectory()) return 0;
        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName, String folder) throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
