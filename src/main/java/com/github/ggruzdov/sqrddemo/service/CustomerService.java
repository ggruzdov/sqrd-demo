package com.github.ggruzdov.sqrddemo.service;

import com.github.ggruzdov.sqrddemo.model.Customer;
import com.github.ggruzdov.sqrddemo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByPhone(phone);
        if (customer.isEmpty()) {
            throw new UsernameNotFoundException("User not found with phone: " + phone);
        }

        return new User(
            customer.get().getPhone(),
            customer.get().getPassword(),
            new ArrayList<>()
        );
    }
}
