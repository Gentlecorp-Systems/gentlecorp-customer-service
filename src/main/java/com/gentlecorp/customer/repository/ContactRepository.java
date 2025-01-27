package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ContactRepository extends MongoRepository<Contact, UUID> {
}
