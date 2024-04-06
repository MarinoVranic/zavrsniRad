package com.vranic.zavrsnirad.service;

import com.vranic.zavrsnirad.model.File;
import com.vranic.zavrsnirad.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public List<File> getAllFiles(){
        return fileRepository.findAll();
    }

    public void uploadFile(MultipartFile datoteka) throws IOException {
        File file = new File();
        file.setFileName(datoteka.getOriginalFilename());
        file.setContentType(datoteka.getContentType());
        file.setSize(datoteka.getSize());
        file.setData(datoteka.getBytes());
        fileRepository.save(file);
    }

    public Long uploadFileReturnId(MultipartFile datoteka) throws IOException {
        File file = new File();
        file.setFileName(datoteka.getOriginalFilename());
        file.setContentType(datoteka.getContentType());
        file.setSize(datoteka.getSize());
        file.setData(datoteka.getBytes());
        fileRepository.save(file);
        return file.getId();
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }

    public List<File> getFileByFileName(String fileName){
        return fileRepository.findByFileName(fileName);
    }
}
