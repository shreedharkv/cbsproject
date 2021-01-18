package com.dfq.coeffi.cbs.admin.service;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import com.dfq.coeffi.cbs.admin.repository.BODDateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class BODDateServiceImpl implements BODDateService{

    @Autowired
    private BODDateRepository bodDateRepository;

    @Override
    public Optional<BODDate> getBODDateByStatus() {
        return ofNullable(bodDateRepository.getBODDateByStatus());
    }

    @Override
    public List<BODDate> getBods(Date date) {
        return bodDateRepository.findByBodDate(date);
    }

    @Override
    public Optional<BODDate> getBODDate(long id) {
        return ofNullable(bodDateRepository.getOne(id));
    }

    @Override
    public BODDate saveBODDate(BODDate bodDate) {
        return bodDateRepository.save(bodDate);
    }

    @Override
    public void checkBOD() {

        BODDate bodDateObject = bodDateRepository.getBODDateByStatus();
        if(bodDateObject == null){
            throw new EntityNotFoundException("BOD Not Started for the Day");
        }

        if(!bodDateObject.getBodStatus()){
            throw new EntityNotFoundException("BOD closed for the day !!!! You cannot proceed any transaction");
        }
    }
}
