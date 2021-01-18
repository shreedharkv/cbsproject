package com.dfq.coeffi.cbs.document;

import com.dfq.coeffi.cbs.exception.FileStorageException;
import com.dfq.coeffi.cbs.exception.TPFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentCategoryRepository documentCategoryRepository;
    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public Document storeFile(MultipartFile file, long documentCategoryId) {

        Document document = new Document();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            document.setFileName(fileName);
            document.setSize(file.getSize());
            document.setFileType(file.getContentType());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(document.getFileName())
                    .toUriString();
            document.setFileDownloadUri(fileDownloadUri);

            DocumentCategory documentCategory = documentCategoryRepository.findOne(documentCategoryId);
            if(documentCategory == null){
                throw new EntityNotFoundException("DocumentCategory id does not exist: "+documentCategoryId);
            }
            document.setDocumentCategory(documentCategory);
            Document persistedDocument = documentRepository.save(document);
            return persistedDocument;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new TPFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new TPFileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public DocumentCategory createDocumentCategory(DocumentCategory documentCategory) {
        return documentCategoryRepository.save(documentCategory);
    }

    @Override
    public DocumentCategory getDocumentCategory(long id) {
        return documentCategoryRepository.findOne(id);
    }

    @Override
    public List<DocumentCategory> getDocumentCategories() {
        return documentCategoryRepository.findAll();
    }

    @Override
    public Document getDocument(long id) {
        return documentRepository.findOne(id);
    }
}