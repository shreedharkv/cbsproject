package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.master.entity.branch.Branch;
import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OccupationRepository extends JpaRepository<Occupation, Long> {
}