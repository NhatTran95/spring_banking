package com.cg.repository;

import com.cg.dto.response.CustomerResDTO;
import com.cg.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.util.List;

public interface ICustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT NEW com.cg.dto.response.CustomerResDTO (" +
            "cus.id, " +
            "cus.fullName, " +
            "cus.email, " +
            "cus.phone, " +
            "cus.balance, " +
            "cus.address" +
            ") FROM Customer AS cus "
    )
    List<CustomerResDTO> findAllCustomerResDTO();

    @Modifying
    @Query("UPDATE Customer as cus " +
            "set cus.balance = cus.balance + :transactionAmount " +
            "where cus.id =:customerId")
    void incrementBalance(@Param("customerId") Long customerId, @Param("transactionAmount")BigDecimal transactionAmount);

    @Modifying
    @Query("UPDATE Customer as cus " +
            "set cus.balance = cus.balance - :transactionAmount " +
            "where cus.id =:customerId")
    void decrementBalance(@Param("customerId") Long customerId, @Param("transactionAmount")BigDecimal transactionAmount);

    List<Customer> findAllByDeleted(Boolean deleted);
}
