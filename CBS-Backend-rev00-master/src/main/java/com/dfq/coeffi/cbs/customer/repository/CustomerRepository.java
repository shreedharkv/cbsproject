package com.dfq.coeffi.cbs.customer.repository;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByStatus(Boolean status);

    @Query("update Customer c set c.status=false where c.id=:id")
    @Modifying
    void deleteCustomer(@Param("id") Long id);
}