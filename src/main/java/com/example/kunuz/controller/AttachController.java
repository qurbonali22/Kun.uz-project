package com.example.kunuz.controller;

import com.example.kunuz.dto.attach.AttachResponseDTO;
import com.example.kunuz.enums.Language;
import com.example.kunuz.enums.ProfileRole;
import com.example.kunuz.service.AttachService;
import com.example.kunuz.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/attach")
public class AttachController {

    private final AttachService service;

    public AttachController(AttachService service) {
        this.service = service;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestHeader(value = "Accept-Language", defaultValue = "RU") Language language) {
        AttachResponseDTO fileName = service.saveToSystem(file, language);
        return ResponseEntity.ok().body(fileName);
    }


    @PreAuthorize("hasRole('USER')") @GetMapping(value = "/open/{fileName}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] open(@PathVariable("fileName") String fileName) {
        return service.open(fileName);
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/open_general/{fileName}", produces = MediaType.ALL_VALUE)
    public byte[] open_general(@PathVariable("fileName") String fileName) {
        return service.open_general(fileName);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/download/{fineName}")
    public ResponseEntity<Resource> download(@PathVariable("fineName") String fileName,
                                             @RequestHeader(value = "Accept-Language", defaultValue = "RU") Language language) {
        Resource file = service.download(fileName, language);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @PreAuthorize("hasRole('MODERATOR')")
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<?> deleteById(@PathVariable("fileName") String fileName) {
        String result = service.deleteById(fileName);
        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<?> getWithPage(@RequestParam("page") Integer page, @RequestParam("size") Integer size,
                                         HttpServletRequest request) {
        HttpRequestUtil.getProfileId(request, ProfileRole.ROLE_ADMIN);
        Page<AttachResponseDTO> result = service.getWithPage(page, size);
        return ResponseEntity.ok(result);
    }

}
