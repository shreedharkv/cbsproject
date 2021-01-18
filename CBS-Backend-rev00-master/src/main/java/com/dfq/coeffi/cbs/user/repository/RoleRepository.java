/**
 *
 */
package com.dfq.coeffi.cbs.user.repository;


import com.dfq.coeffi.cbs.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author H Kapil Kumar
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByActive(Boolean active);

}