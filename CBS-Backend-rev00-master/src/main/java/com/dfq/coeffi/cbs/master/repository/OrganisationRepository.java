package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import com.dfq.coeffi.cbs.master.entity.organisation.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    Organisation findOrganisationByCode(String code);
}