/**
 *
 */
package com.dfq.coeffi.cbs.user.repository;

import com.dfq.coeffi.cbs.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


/**
 * @author H Kapil Kumar
 */


@Transactional
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u join fetch u.roles r" +
            " where u.email=:email and u.active=true")
    public Optional<User> findUserByEmail(@Param("email") String email);

    List<User> findByActive(Boolean active);

    @Query("update User u set u.active=false where u.id=:id")
    @Modifying
    void deleteUser(@Param("id") Long id);
}