package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import com.dfq.coeffi.cbs.master.entity.organisation.Organisation;
import com.dfq.coeffi.cbs.master.repository.OccupationRepository;
import com.dfq.coeffi.cbs.master.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrganisationServiceImpl implements OrganisationService {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    @Override
    public Organisation getOrganisation(long id) {
        return organisationRepository.findOne(id);
    }

    @Override
    public List<Organisation> getOrganisations() {
        return organisationRepository.findAll();
    }

    @Override
    public Organisation findOrganisationByCOde(String code) {
        return organisationRepository.findOrganisationByCode(code);
    }

    @Override
    public Occupation getOccupation(long id) {
        return occupationRepository.findOne(id);
    }

    @Override
    public List<Occupation> getOccupations() {
        return occupationRepository.findAll();
    }
}