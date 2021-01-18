package com.dfq.coeffi.cbs.document;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

public interface FileStorageService {

    Document storeFile(MultipartFile file, long documentCategoryId);
    Resource loadFileAsResource(String fileName);

    DocumentCategory createDocumentCategory(DocumentCategory documentCategory);
    DocumentCategory getDocumentCategory(long id);
    List<DocumentCategory> getDocumentCategories();
    Document getDocument(long id);
}