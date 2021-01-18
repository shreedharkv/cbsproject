package com.dfq.coeffi.cbs.bank.api;

import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.document.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@RestController
public class BankOpenController {

    @Autowired
    private BankService bankService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("active-bank")
    public ResponseEntity<BankMaster> getActiveBank(){
        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if(!bankMasterObj.isPresent()){
            throw new EntityNotFoundException("No active bank found");
        }
        BankMaster bankMaster = bankMasterObj.get();
        return new ResponseEntity<>(bankMaster, HttpStatus.OK);
    }

    @GetMapping("/file-view/{fileName:.+}")
    public byte[] viewImage(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        File serverFile = new File(resource.getURI());
        return Files.readAllBytes(serverFile.toPath());
    }

}