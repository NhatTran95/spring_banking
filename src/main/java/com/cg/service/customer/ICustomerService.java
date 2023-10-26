package com.cg.service.customer;

import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Withdraw;
import com.cg.model.dto.CustomerResDTO;
import com.cg.model.dto.TransferReqDTO;
import com.cg.service.IGeneralService;

import java.util.List;

public interface ICustomerService extends IGeneralService<Customer, Long> {

    List<CustomerResDTO> findAllCustomerResDTO(Boolean deleted);

    Customer create(Customer customer);

    Customer update(Customer customer);

    void deposit(Deposit deposit);

    void withdraw(Withdraw withdraw);

    void transfer(TransferReqDTO transferReqDTO);
}
