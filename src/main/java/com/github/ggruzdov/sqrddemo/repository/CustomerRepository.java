package com.github.ggruzdov.sqrddemo.repository;

import com.github.ggruzdov.sqrddemo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByPhone(String phone);
}