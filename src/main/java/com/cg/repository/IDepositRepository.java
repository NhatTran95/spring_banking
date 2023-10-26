package com.cg.repository;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.dto.DepositResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDepositRepository extends JpaRepository<Deposit, Long> {

    @Query("SELECT NEW com.cg.model.dto.DepositResDTO (" +
            "dep.id, " +
            "dep.transactionAmount, " +
            "dep.createdAt" +
            ") " +
            "FROM Deposit AS dep " +
            "WHERE dep.customer = :customer"
    )
    List<DepositResDTO> findAllDepositResDTOS(@Param("customer") Customer customer);

}
