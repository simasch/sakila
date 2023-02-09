package ch.martinelli.sakila.controller;

import ch.martinelli.sakila.backend.entity.CustomerListEntry;
import ch.martinelli.sakila.backend.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@RequestMapping("/customers")
@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String getCustomers(Model model) {
        List<CustomerListEntry> customers = customerRepository.findAll(Collections.emptyList(), 0, 50);
        model.addAttribute("customers", customers);

        return "customers";
    }
}
