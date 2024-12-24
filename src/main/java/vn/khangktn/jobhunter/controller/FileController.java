package vn.khangktn.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.khangktn.jobhunter.domain.response.ResUploadFile;
import vn.khangktn.jobhunter.service.FileService;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;
import vn.khangktn.jobhunter.util.errorException.FileException;

@RestController
@RequestMapping("api/v1/files")
public class FileController {
    private final FileService fileService;

    FileController(FileService fileService){
        this.fileService = fileService;
    }

    @PostMapping("")
    @ApiMessage("Upload file succeed!")
    public ResponseEntity<ResUploadFile> upload(@RequestParam(name = "file", required = false) MultipartFile file, @RequestParam("folder") String folder)
        throws URISyntaxException, IOException, FileException{
        // Validate file isEmpty
        if(file == null || file.isEmpty()) throw new FileException("File is null!");
        
        // Validate file format
        List<String> extensionAllowedList = Arrays.asList(".doc", ".img", ".png", ".jpeg", ".jpg");
        boolean isValid = extensionAllowedList.stream().anyMatch(extension -> file.getOriginalFilename().toLowerCase().endsWith(extension));
        if(!isValid) throw new FileException("File format isn't supported!");

        // Validate file size: <= 5 MB
        if(file.getSize() / 1024 / 1024.0 > 5) throw new FileException("File size must be < 5 MB!");

        // Create new folder if isn't exist
        fileService.createFolder(folder);

        // Store file
        String uploadFile = fileService.store(file, folder);
        ResUploadFile resUploadFile = new ResUploadFile(uploadFile, Instant.now());
        return ResponseEntity.ok(resUploadFile);
    }

    @GetMapping("")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws FileException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new FileException("Missing required params: (fileName or folder) in query params.");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new FileException("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentLength(fileLength)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

}
