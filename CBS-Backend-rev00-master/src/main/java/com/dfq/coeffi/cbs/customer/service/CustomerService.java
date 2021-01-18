package com.dfq.coeffi.cbs.customer.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer> customers();

    Optional<Customer> getCustomer(long id);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(long id);
}