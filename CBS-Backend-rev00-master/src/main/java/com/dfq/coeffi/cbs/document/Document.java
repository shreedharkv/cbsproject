package com.dfq.coeffi.cbs.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@ToString
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    @OneToOne
    private DocumentCategory documentCategory;

}