package com.cg.service.history;


import com.cg.model.Customer;
import com.cg.model.dto.CustomerDepositResDTO;
import com.cg.model.dto.DepositResDTO;
import com.cg.repository.ICustomerRepository;
import com.cg.repository.IDepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HistoryServiceImpl implements IHistoryService {

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IDepositRepository depositRepository;


    @Override
    public CustomerDepositResDTO getInfoAndDeposits(Customer customer) {

        CustomerDepositResDTO customerDepositResDTO = customer.toCustomerDepositResDTO();

        List<DepositResDTO> depositResDTOS = depositRepository.findAllDepositResDTOS(customer);


        customerDepositResDTO.setDeposits(depositResDTOS);

        return customerDepositResDTO;
    }
    @Override
    public List<CustomerDepositResDTO> getAllDeposit(List<Customer> customers){
        List<CustomerDepositResDTO> customerDepositResDTOS = new ArrayList<>();
        for(Customer customer : customers){
            CustomerDepositResDTO customerDepositResDTO = customer.toCustomerDepositResDTO();

            List<DepositResDTO> depositResDTOS = depositRepository.findAllDepositResDTOS(customer);

            customerDepositResDTO.setDeposits(depositResDTOS);

            customerDepositResDTOS.add(customerDepositResDTO);
        }
        return customerDepositResDTOS;
    }
}
