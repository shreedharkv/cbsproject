package com.dfq.coeffi.cbs.customer.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> customers() {
        return customerRepository.findByStatus(true);
    }

    @Override
    public Optional<Customer> getCustomer(long id) {
        return ofNullable(customerRepository.getOne(id));
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        customer.setStatus(true);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(long id) {
        customerRepository.deleteCustomer(id);
    }
}