package com.netpos.desafionetpos.repository;

import com.netpos.desafionetpos.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT product FROM Product product " +
            "LEFT JOIN product.stock stock " +
            "WHERE product.userAccount.id = :userAccountId " +
            "AND (product.name LIKE %:filter% OR product.code LIKE %:filter%)")
    List<Product> findByFilters(@Param("userAccountId") Long userAccountId, @Param("filter") String filter, Sort sort);

    Optional<Product> findOneByCodeAndUserAccount_Id(String code, Long userAccountId);

    Optional<Product> findOneByIdAndUserAccount_Id(Long id, Long userAccountId);

}
