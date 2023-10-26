package com.cg.service.customer;

import com.cg.exception.DataInputException;
import com.cg.model.*;
import com.cg.model.dto.CustomerResDTO;
import com.cg.model.dto.TransferReqDTO;
import com.cg.repository.*;
import com.cg.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private ILocationRegionRepository locationRegionRepository;

    @Autowired
    private IDepositRepository depositRepository;

    @Autowired
    private IWithdrawRepository withdrawRepository;

    @Autowired
    private ITransferRepository transferRepository;


    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }


    @Override
    public List<CustomerResDTO> findAllCustomerResDTO(Boolean deleted) {
        return customerRepository.findAllCustomerResDTO(deleted);
    }

    @Override
    public Customer create(Customer customer) {
        LocationRegion locationRegion = customer.getLocationRegion();
        locationRegionRepository.save(locationRegion);

        customer.setLocationRegion(locationRegion);
        customer.setBalance(BigDecimal.ZERO);
        customerRepository.save(customer);

        return customer;
    }

    @Override
    public Customer update(Customer customer) {

        Customer oldCus = findById(customer.getId()).get();
        customer.setBalance(oldCus.getBalance());
        customer.setDeleted(oldCus.getDeleted());
        locationRegionRepository.save(customer.getLocationRegion());
        customerRepository.save(customer);

        return customer;
    }


    @Override
    public void deposit(Deposit deposit) {
        depositRepository.save(deposit);

        Long customerId = deposit.getCustomer().getId();
        BigDecimal transactionAmount = deposit.getTransactionAmount();
        customerRepository.incrementBalance(customerId, transactionAmount);
    }

    @Override
    public void withdraw(Withdraw withdraw){
        withdrawRepository.save(withdraw);

        Long customerId = withdraw.getCustomer().getId();
        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        customerRepository.decrementBalance(customerId, transactionAmount);
    }

    @Override
    public void transfer(TransferReqDTO transferReqDTO) {
        if (!ValidateUtils.isNumberValid(transferReqDTO.getSenderId())) {
            throw new DataInputException("Mã người gửi không hợp lệ");
        }

        if (!ValidateUtils.isNumberValid(transferReqDTO.getRecipientId())) {
            throw new DataInputException("Mã người nhận không hợp lệ");
        }

        if (!ValidateUtils.isNumberValid(transferReqDTO.getTransferAmount())) {
            throw new DataInputException("Số tiền giao dịch không hợp lệ");
        }

        Long senderId = Long.parseLong(transferReqDTO.getSenderId());
        Long recipientId = Long.parseLong(transferReqDTO.getRecipientId());

        if (senderId.equals(recipientId)) {
            throw new DataInputException("Mã người gửi và người nhận phải khác nhau");
        }

        String transferAmountStr = transferReqDTO.getTransferAmount();

        if (transferAmountStr.length() > 7) {
            throw new DataInputException("Số tiền chuyển khoản tối đa là $1.000.000");
        }

        BigDecimal transferAmount = BigDecimal.valueOf(Long.parseLong(transferAmountStr));

        Customer sender = customerRepository.findById(senderId).orElseThrow(() -> {
            throw new DataInputException("Mã người gửi không tồn tại");
        });

        Customer recipient = customerRepository.findById(recipientId).orElseThrow(() -> {
            throw new DataInputException("Mã người nhận không tồn tại");
        });

        BigDecimal minValue = BigDecimal.valueOf(1000);
        BigDecimal maxValue = BigDecimal.valueOf(1000000);

        if (transferAmount.compareTo(minValue) < 0) {
            throw new DataInputException("Số tiền chuyển khoản ít nhất là $1.000");
        }

        if (transferAmount.compareTo(maxValue) > 0) {
            throw new DataInputException("Số tiền chuyển khoản tối đa là $1.000.000");
        }

        long fees = 10L;
        BigDecimal feesAmount = transferAmount.multiply(BigDecimal.valueOf(fees)).divide(BigDecimal.valueOf(100));
        BigDecimal transactionAmount = transferAmount.add(feesAmount);

        BigDecimal senderCurrentBalance = sender.getBalance();

        if (transactionAmount.compareTo(senderCurrentBalance) > 0) {
            throw new DataInputException("Số dư người gửi không đủ để thực hiện chuyển khoản");
        }

        try {
            customerRepository.decrementBalance(senderId, transactionAmount);

            Transfer transfer = new Transfer();
            transfer.setSender(sender);
            transfer.setRecipient(recipient);
            transfer.setTransferAmount(transferAmount);
            transfer.setFees(fees);
            transfer.setFeesAmount(feesAmount);
            transfer.setTransactionAmount(transactionAmount);
            transferRepository.save(transfer);

            customerRepository.incrementBalance(recipientId, transferAmount);
        }
        catch (Exception ex) {
            throw new DataInputException("Vui lòng liên hệ Administrator");
        }
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Customer customer) {

    }

    @Override
    public void deleteById(Long aLong) {

    }
}
