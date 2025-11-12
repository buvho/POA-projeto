package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository repo;
    
    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public List<Customer> findAll() {
        return repo.findAll();
    }

    public Optional<Customer> findById(Long id) { 
        return repo.findById(id);
    }

    public Customer create(Customer customer) {
        return repo.save(customer);
    }

    public Customer update(Long id, Customer updated) {
        return repo.findById(id).map(c -> {
            c.setName(updated.getName());
            c.setEmail(updated.getEmail());
            c.setPhone(updated.getPhone());
            return repo.save(c);
        }).orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
