package com.dfq.coeffi.cbs.admin.service;

import com.dfq.coeffi.cbs.admin.entity.BODDate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BODDateService {

    Optional<BODDate> getBODDateByStatus();
    List<BODDate> getBods(Date date);

    Optional<BODDate> getBODDate(long id);

    BODDate saveBODDate(BODDate bodDate);

    void checkBOD();

}