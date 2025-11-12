package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // aqui você pode adicionar consultas customizadas: findByEmail, etc.

    // find + By + Email = SELECT * FROM WHERE email = ?
    Optional<Customer> findByEmail(String email);
}
