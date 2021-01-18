package com.netpos.desafionetpos.repository;

import com.netpos.desafionetpos.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    List<UserAccount> findByFullNameStartingWithIgnoreCaseOrderByFullName(String term);

    List<UserAccount> findByOrderByFullName();

    Optional<UserAccount> findOneByEmail(String email);

}
