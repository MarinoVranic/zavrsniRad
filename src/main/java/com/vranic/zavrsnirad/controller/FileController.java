package com.vranic.zavrsnirad.controller;

import com.vranic.zavrsnirad.model.Dobavljac;
import com.vranic.zavrsnirad.model.File;
import com.vranic.zavrsnirad.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping("/all")
    public String showFileUploadPage(Model model) {
        model.addAttribute("files", fileService.getAllFiles());
        return "file/file"; // Assuming your Thymeleaf file is named fileUploadDownload.html
    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable(value = "id") Long id){
        fileService.deleteById(id);
        return "redirect:/file/all";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        fileService.uploadFile(file);
        return "redirect:/file/all";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        File fileEntity = fileService.getFileById(id);
        if (fileEntity != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileEntity.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
                    .body(fileEntity.getData());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/find")
    public String findFileByFileName(@RequestParam("fileName") String fileName, Model model){
        List<File> fileList = fileService.getFileByFileName(fileName);
        if (fileList.isEmpty()) {
            model.addAttribute("error", "Datoteka tog naziva ne postoji u sustavu!");
            model.addAttribute("files", fileService.getAllFiles());
            File file = new File();
            model.addAttribute("file", file);
        } else {
            model.addAttribute("files", fileList);
            File file = new File();
            model.addAttribute("file", file);
        }
        return "file/file";
    }
}
