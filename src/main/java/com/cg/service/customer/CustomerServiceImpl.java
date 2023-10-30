package com.cg.service.customer;

import com.cg.dto.response.CustomerResDTO;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.repository.ICustomerRepository;
import com.cg.repository.IDepositRepository;
import com.cg.repository.ITransferRepository;
import com.cg.repository.IWithdrawRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final ICustomerRepository customerRepository;

    private final IDepositRepository depositRepository;

    private final IWithdrawRepository withdrawRepository;

    private final ITransferRepository transferRepository;

    @Override
    public List<CustomerResDTO> findAllCustomerResDTO() {
        return customerRepository.findAllCustomerResDTO();
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {

        return customerRepository.findById(id);
    }

    @Override
    public void create(Customer customer) {
        customer.setBalance(BigDecimal.ZERO);
        customerRepository.save(customer);
    }

    @Override
    public void update(Long id, Customer customer) {
        Optional<Customer> customerOld = customerRepository.findById(id);
        customer.setBalance(customerOld.get().getBalance());
        customerRepository.save(customer);

    }

    @Override
    public void removeById(Long id) {
        customerRepository.deleteById(id);

    }

    @Override
    public void deposit(Deposit deposit) {
        deposit.setDepositDate(new Date());
        depositRepository.save(deposit);

        Long idCustomer = deposit.getCustomer().getId();
        BigDecimal transactionAmount = deposit.getTransactionAmount();
        customerRepository.incrementBalance(idCustomer, transactionAmount);
    }

    @Override
    public void withdraw(Withdraw withdraw) {
        withdraw.setWithdrawDate(new Date());
        withdrawRepository.save(withdraw);

        Long idCustomer = withdraw.getCustomer().getId();
        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        customerRepository.decrementBalance(idCustomer, transactionAmount);
    }

    @Override
    public void transfer(Transfer transfer) {
        transfer.setTransferDate(new Date());
        transferRepository.save(transfer);

        Long idSender = transfer.getSender().getId();
        Long idRecipient = transfer.getRecipient().getId();
        BigDecimal transferAmount = transfer.getTransferAmount();
        BigDecimal transactionAmount = transfer.getTransactionAmount();

        customerRepository.decrementBalance(idSender, transactionAmount);
        customerRepository.incrementBalance(idRecipient, transferAmount);

    }

    @Override
    public List<Customer> findAllWithoutId(Long id) {
        List<Customer> customers = customerRepository.findAllByDeleted(false);
        return customers.stream().filter(cus -> !cus.getId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findAllTransfer(){
        List<Transfer> transfers = transferRepository.findAll();
        return transfers;
    }


}
