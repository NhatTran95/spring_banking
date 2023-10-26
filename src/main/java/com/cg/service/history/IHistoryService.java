package com.cg.service.history;

import com.cg.model.Customer;
import com.cg.model.dto.CustomerDepositResDTO;
import com.cg.model.dto.CustomerResDTO;

import java.util.List;

public interface IHistoryService {

    CustomerDepositResDTO getInfoAndDeposits(Customer customer);
    List<CustomerDepositResDTO> getAllDeposit(List<Customer> customers);
}
