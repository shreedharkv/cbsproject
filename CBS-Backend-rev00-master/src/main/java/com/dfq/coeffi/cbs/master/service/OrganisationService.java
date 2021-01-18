package com.dfq.coeffi.cbs.master.service;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import com.dfq.coeffi.cbs.master.entity.organisation.Organisation;

import java.util.List;

public interface OrganisationService {

    Organisation getOrganisation(long id);
    List<Organisation> getOrganisations();
    Organisation findOrganisationByCOde(String code);

    Occupation getOccupation(long id);
    List<Occupation> getOccupations();
}