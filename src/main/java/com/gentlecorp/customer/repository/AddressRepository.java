package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findByCity(String city);
}
