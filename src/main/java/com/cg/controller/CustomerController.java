package com.cg.controller;

import com.cg.dto.response.CustomerResDTO;
import com.cg.model.Customer;
import com.cg.model.Deposit;
import com.cg.model.Transfer;
import com.cg.model.Withdraw;
import com.cg.service.customer.CustomerServiceImpl;
import com.cg.service.customer.ICustomerService;

import com.cg.service.transfer.ITransferService;
import com.cg.service.transfer.TransferServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private final ICustomerService customerService;

//    private final HttpSession httpSession;

//    List<Customer> customersList = new ArrayList<>();


//    @GetMapping
//    public String showListPage(Model model,
//                               @CookieValue(value = "count", defaultValue = "0") Long count, HttpServletResponse response) {
//        List<CustomerResDTO> customers = customerService.findAllCustomerResDTO();
//        count++;
//        Cookie cookie = new Cookie("count", count.toString());
//        cookie.setMaxAge(60);
//        response.addCookie(cookie);
//        model.addAttribute("cookie", cookie);
//        model.addAttribute("customers", customers);
//
//        return "customer/list";
//    }
    @GetMapping
    public String showListPage(Model model) {
        List<CustomerResDTO> customers = customerService.findAllCustomerResDTO();

        model.addAttribute("customers", customers);

        return "customer/list";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model ){
        model.addAttribute("customer", new Customer());


        return "customer/create";
    }

    @PostMapping("/create")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {

        if (customer.getFullName().length() == 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Created unsuccessful");
        } else {
            customerService.create(customer);

            model.addAttribute("success", true);
            model.addAttribute("message", "Created successfully");
            model.addAttribute("customer", new Customer());
        }

        return "customer/create";
    }

//    @PostMapping("/create")
//    public String createCustomer(@ModelAttribute Customer customer, Model model) {
//
//        if (customer.getFullName().length() == 0) {
//            model.addAttribute("success", false);
//            model.addAttribute("message", "Created unsuccessful");
//        } else {
//            customerService.create(customer);
//            customersList.add(customer);
//            httpSession.setAttribute("customers", customersList);
//
//            model.addAttribute("success", true);
//            model.addAttribute("message", "Created successfully");
//            model.addAttribute("customer", new Customer());
//        }
//
//        return "customer/create";
//    }


//    @GetMapping("/viewSession")
//    public  String viewCustomer(Model model){
//        List<Customer> customersView = (List<Customer>) httpSession.getAttribute("customers");
//        model.addAttribute("customers", customersView);
//        return "customer/viewCustomer";
//    }

    @GetMapping("/update/{id}")
    public String showUpdatePage(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found customer");
            model.addAttribute("customer", new Customer());
        } else {
            model.addAttribute("customer", customer);
        }

        return "customer/update";
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@ModelAttribute Customer customer, @PathVariable Long id, Model model) {
        customerService.update(id, customer);
        model.addAttribute("success", true);
        model.addAttribute("message", "Updated successfully");

        return "customer/update";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found customer to delete");
            model.addAttribute("customers", customerService.findAllCustomerResDTO());
        } else {

            model.addAttribute("customer", customer);
        }

        return "customer/delete";
    }

    @PostMapping("delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        customerService.removeById(id);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "Deleted successfully");

        return "redirect:/customers";
    }

    @GetMapping("/deposit/{id}")
    public String showDepositPage(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found customer");
            model.addAttribute("deposit", new Deposit());
        } else {
            Deposit deposit = new Deposit();
            deposit.setCustomer(customer.get());
            model.addAttribute("deposit", deposit);
        }

        return "customer/deposit";
    }

    @PostMapping("/deposit/{id}")
    public String deposit(@ModelAttribute Deposit deposit, Model model, @PathVariable Long id) {
        Optional<Customer> customer = customerService.findById(id);

        if(deposit.getTransactionAmount() == null) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("deposit", deposit);
            return "customer/deposit";
        }

        BigDecimal transactionAmount = deposit.getTransactionAmount();


        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("deposit", deposit);
            return "customer/deposit";
        }

//        deposit.setCustomer(customer.get());
        customerService.deposit(deposit);
        Optional<Customer> customerNew = customerService.findById(id);
        deposit.setCustomer(customerNew.get());

        deposit.setTransactionAmount(null);

        model.addAttribute("success", true);
        model.addAttribute("message", "Deposited successfully");

        model.addAttribute("deposit", deposit);

        return "customer/deposit";
    }

    @GetMapping("/withdraw/{id}")
    public String showWithdrawPage(@PathVariable Long id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found customer");
            model.addAttribute("withdraw", new Withdraw());
        } else {
            Withdraw withdraw = new Withdraw();
            withdraw.setCustomer(customer.get());
            model.addAttribute("withdraw", withdraw);
        }
        return "customer/withdraw";
    }

    @PostMapping("/withdraw/{id}")
    public String withdraw(@ModelAttribute Withdraw withdraw, Model model, @PathVariable Long id) {
        Optional<Customer> customer = customerService.findById(id);

        if(withdraw.getTransactionAmount() == null) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("withdraw", withdraw);
            return "customer/withdraw";
        }

        BigDecimal transactionAmount = withdraw.getTransactionAmount();
        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("withdraw", withdraw);
            return "customer/withdraw";
        }
        BigDecimal currentBalance = customer.get().getBalance();
        BigDecimal newBalance = currentBalance.subtract(transactionAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Withdrawal amount exceeds the limit");
            model.addAttribute("withdraw", withdraw);
            return "customer/withdraw";
        }


        customerService.withdraw(withdraw);
        Optional<Customer> customerNew = customerService.findById(id);
        withdraw.setCustomer(customerNew.get());
        withdraw.setTransactionAmount(null);

        model.addAttribute("success", true);
        model.addAttribute("message", "Withdraw successfully");
        model.addAttribute("withdraw", withdraw);

        return "customer/withdraw";
    }

    @GetMapping("/transfer/{id}")
    public String showTransferPage(@PathVariable Long id, Model model) {
        Optional<Customer> sender = customerService.findById(id);
        Transfer transfer = new Transfer();
        List<Customer> recipients = customerService.findAllWithoutId(id);

        if (sender.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found customer");
            model.addAttribute("recipients", recipients);
            model.addAttribute("transfer", new Transfer());
        } else {
            transfer.setSender(sender.get());
            model.addAttribute("transfer", transfer);
            model.addAttribute("recipients", recipients);
        }
        return "customer/transfer";
    }

    @PostMapping("/transfer/{id}")
    public String transfer(@PathVariable Long id, Model model, @ModelAttribute Transfer transfer) {
        Optional<Customer> sender = customerService.findById(id);
        Optional<Customer> recipient = customerService.findById(transfer.getRecipient().getId());

        if (sender.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found sender");
            return "customer/transfer";
        }

        if (recipient.isEmpty()) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Not found recipient");
            model.addAttribute("transfer", transfer);
            return "customer/transfer";
        }

        List<Customer> recipients = customerService.findAllWithoutId(transfer.getSender().getId());
        BigDecimal fee = BigDecimal.valueOf(10);

        if(transfer.getTransferAmount() == null) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("transfer", transfer);
            return "customer/transfer";
        }

        BigDecimal transferAmount = transfer.getTransferAmount();

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Please enter the amount as a non-negative number");
            model.addAttribute("recipients", recipients);
            return "customer/transfer";
        }

        BigDecimal feesAmount = transferAmount.multiply(fee.divide(BigDecimal.valueOf(100)));
        BigDecimal transactionAmount = feesAmount.add(transferAmount);
        if (sender.get().getBalance().compareTo(transactionAmount) < 0) {
            model.addAttribute("success", false);
            model.addAttribute("message", "The account is not enough to make transactions");
            model.addAttribute("recipients", recipients);
            return "customer/transfer";
        }

        transfer.setSender(sender.get());
        transfer.setRecipient(recipient.get());
        transfer.setFees(fee.longValue());
        transfer.setFeesAmount(feesAmount);
        transfer.setTransferAmount(transferAmount);
        transfer.setTransactionAmount(transactionAmount.setScale(0, RoundingMode.HALF_UP));

        customerService.transfer(transfer);

        Optional<Customer> senderNew = customerService.findById(transfer.getSender().getId());

        Transfer transferView = new Transfer();
        transferView.setSender(senderNew.get());
        transferView.setTransferAmount(null);

        model.addAttribute("success", true);
        model.addAttribute("message", "Transfer successfully");
        model.addAttribute("transfer", transferView);
        model.addAttribute("recipients", recipients);


        return "customer/transfer";


    }

    @GetMapping("/transferHistory")
    public String showHistoryPage(Model model) {
        List<Transfer> transfers = customerService.findAllTransfer();
        model.addAttribute("transfers", transfers);
        return "customer/transferHistory";
    }


}
