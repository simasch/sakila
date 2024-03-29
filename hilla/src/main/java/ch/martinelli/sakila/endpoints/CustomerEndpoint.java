package ch.martinelli.sakila.endpoints;

import ch.martinelli.sakila.backend.entity.CustomerListEntry;
import ch.martinelli.sakila.backend.repository.CustomerRepository;
import ch.martinelli.sakila.db.tables.records.CustomerRecord;
import dev.hilla.Endpoint;
import dev.hilla.Nonnull;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RolesAllowed({"ADMIN", "USER"})
@Endpoint
public class CustomerEndpoint {

    private final CustomerRepository customerRepository;

    public CustomerEndpoint(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Nonnull
    public List<@Nonnull CustomerListEntry> findAll(Pageable page) {
        Sort sort = page.getSort();
        return customerRepository.findAll(Collections.emptyList(), (int) page.getOffset(), page.getPageSize());
    }

    public int count() {
        return customerRepository.count();
    }

    public Optional<Customer> findById(@Nonnull Integer id) {
        return customerRepository.findById(id)
                .map(customerRecord -> new Customer(customerRecord.getCustomerId(), customerRecord.getFirstName(), customerRecord.getLastName(), customerRecord.getActivebool()));
    }

    @Nonnull
    public Customer save(@Nonnull Customer customer) {
        CustomerRecord customerRecord;
        if (customer.getId() == null) {
            customerRecord = customerRepository.newRecord();
        } else {
            customerRecord = customerRepository.findById(customer.getId()).orElseThrow();
        }
        customerRecord.setFirstName(customer.getFirstName());
        customerRecord.setLastName(customer.getLastName());
        customerRecord.setActivebool(customer.isActivebool());

        CustomerRecord savedCustomer = customerRepository.save(customerRecord);
        return new Customer(savedCustomer.getCustomerId(), savedCustomer.getFirstName(), savedCustomer.getLastName(), savedCustomer.getActivebool());
    }
}
