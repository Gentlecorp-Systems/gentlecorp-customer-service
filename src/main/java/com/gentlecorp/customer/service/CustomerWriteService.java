package com.gentlecorp.customer.service;

import com.gentlecorp.customer.MailProps;
import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.IllegalArgumentException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.PasswordInvalidException;
import com.gentlecorp.customer.exception.UsernameExistsException;
import com.gentlecorp.customer.model.dto.AccountDTO;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.repository.ContactRepository;
import com.gentlecorp.customer.repository.CustomerRepository;
import com.gentlecorp.customer.security.CustomUserDetails;
import com.gentlecorp.customer.security.enums.RoleType;
import com.gentlecorp.customer.security.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gentlecorp.customer.model.enums.StatusType.ACTIVE;
import static com.gentlecorp.customer.security.enums.RoleType.ADMIN;
import static com.gentlecorp.customer.util.Constants.LOWERCASE;
import static com.gentlecorp.customer.util.Constants.MIN_LENGTH;
import static com.gentlecorp.customer.util.Constants.NUMBERS;
import static com.gentlecorp.customer.util.Constants.SYMBOLS;
import static com.gentlecorp.customer.util.Constants.UPPERCASE;
import static com.gentlecorp.customer.util.Validation.validateContact;
import static com.gentlecorp.customer.util.VersionUtils.validateVersion;
import static java.util.Locale.GERMAN;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteService {

    private final CustomerReadService customerReadService;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final MailService mailService;
    private final MailProps props;
    private final KeycloakService keycloakService;

    public Customer create(final Customer customer, final String password) {
        customer.setCustomerState(ACTIVE);
        log.debug("create: customer={}", customer);
        log.debug("create: address={}", customer.getAddress());

        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new EmailExistsException(customer.getEmail());

        final var username = customer.getUsername();
        customer.setUsername(username.toLowerCase(GERMAN));
        final var isUsernameExisting = customerRepository.existsByUsername(username);
        if (isUsernameExisting)
            throw new UsernameExistsException(username);

        if (!checkPassword(password)) {
            throw new PasswordInvalidException(password);
        }

        customer.setId(UUID.randomUUID());

        log.warn("create: customer={}", customer);

        final var role = switch (customer.getTierLevel()) {
            case 1 -> "Basic";
            case 2 -> "Elite";
            case 3 -> "Supreme";
            default -> throw new IllegalArgumentException(customer.getTierLevel());
        };

        keycloakService.signIn(customer, password, role);
        final var checkingAccount = new AccountDTO(
            new BigDecimal(0),
            "CH",
            2,
            50,
            20,
            customer.getId()
        );

        final var customerDb = customerRepository.save(customer);
        log.warn("create: customerDb={}", customerDb);
        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        props.setTo(customerDb.getEmail());
        mailService.send(customerDb);

        log.debug("create: customerDb={}", customerDb);
        return customerDb;
    }

    public Customer update(Customer customerInput, UUID id, int version, final CustomUserDetails user) {
        log.debug("update: id={}, version={}, customer={}", id, version, customerInput);

        customerInput.setCustomerState(ACTIVE);
        final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
        validateVersion(version, customerDb);
        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN) && !customerDb.getUsername().equals(user.getUsername())) {
            throw new AccessForbiddenException(user.getUsername(), roles);
        }
        final var isAdmin = roles.contains(ADMIN);
        final var oldUsername = customerDb.getUsername();
        log.debug("update: updatedCustomerDB.username={}", customerDb.getUsername());

        if (customerInput.getEmail() != null) {
            final var email = customerInput.getEmail();
            if (!Objects.equals(email, customerDb.getEmail()) && customerRepository.existsByEmail(email)) {
                log.error("update: email {} already exists", email);
                throw new EmailExistsException(email);
            }
        }

        if (customerInput.getUsername() != null) {
            final var username = customerInput.getUsername();
            customerInput.setUsername(username.toLowerCase(GERMAN));
            final var isUsernameExisting = customerRepository.existsByUsername(username);
            if (isUsernameExisting && !username.equals(oldUsername)) {
                log.error("update: username {} already exists", username);
                throw new UsernameExistsException(username);
            }
        }

        log.trace("update: No conflict with the email address");
        customerDb.set(customerInput);
        final var updatedCustomerDb = customerRepository.save(customerDb);
        log.debug("update: updatedCustomerDB={}", customerDb);

        keycloakService.update(updatedCustomerDb, user.getJwt(), isAdmin, oldUsername);
        return updatedCustomerDb;
    }

    public UUID addContact(final UUID customerId, final Contact contactInput, CustomUserDetails user) {
        log.debug("addContact: customerId={}, contactInput={}", customerId, contactInput);

        final var customerDb = customerReadService.findById(customerId, user);

        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN) && !customerDb.getUsername().equals(user.getUsername())) {
            throw new AccessForbiddenException(user.getUsername(),roles);
        }

        // Sicherstellen, dass ContactIds nie `null` ist
        if (customerDb.getContactIds() == null) {
            customerDb.setContactIds(new ArrayList<>());
        }

        final var existingContacts = customerDb.getContactIds().stream()
            .map(contactRepository::findById)  // `Optional<Contact>` wird erzeugt
            .flatMap(Optional::stream)         // Entfernt `Optional.empty()`
            .collect(Collectors.toList());      // Liste von `Contact` Objekten

        log.debug("addContact: contacts={}", existingContacts);
        validateContact(contactInput, existingContacts);
        contactInput.setId(UUID.randomUUID());

        final var contactDb = contactRepository.save(contactInput);
        log.debug("addContact: contactDb={}", contactDb);

        customerDb.getContactIds().add(contactDb.getId());
        customerRepository.save(customerDb);
        log.debug("addContact: customerDb={}", customerDb);

        return contactDb.getId();
    }

    public Contact updateContact(final UUID customerId, final int customerVersion,  final UUID contactId, final int contactVersion, final Contact contactInput, final CustomUserDetails user) {
        log.debug("updateContact: customerId={},customerVersion={}, contactId={}, contactVersion={}, contactInput={}", customerId, customerVersion, contactId, contactVersion, contactInput);

        final var customerDb = customerReadService.findById(customerId, user);

        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN) && !customerDb.getUsername().equals(user.getUsername())) {
            throw new AccessForbiddenException(user.getUsername(),roles);
        }

        if (!customerDb.getContactIds().contains(contactId)) {
            throw new NotFoundException(contactId);
        }

        final var contactDb = contactRepository.findById(contactId).orElseThrow(() -> new NotFoundException(contactId));
        validateContact(contactInput, contactDb, contactId);
        validateVersion(contactVersion, contactDb);
        contactDb.set(contactInput);
        contactRepository.save(contactDb);
        log.debug("updateContact: contactDb={}", contactDb);
        customerRepository.save(customerDb);
        return contactDb;
    }

    public boolean removeContact(final UUID customerId, final int customerVersion,  final UUID contactId, final int contactVersion, final CustomUserDetails user) {
        log.debug("removeContact: customerId={},customerVersion={}, contactId={}, contactVersion={}", customerId, customerVersion, contactId, contactVersion);

        final var customerDb = customerReadService.findById(customerId, user);

        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN) && !customerDb.getUsername().equals(user.getUsername())) {
            throw new AccessForbiddenException(user.getUsername(),roles);
        }

        if (!customerDb.getContactIds().contains(contactId)) {
            throw new NotFoundException(contactId);
        }

        final var contactDb = contactRepository.findById(contactId).orElseThrow(() -> new NotFoundException(contactId));
        validateVersion(contactVersion, contactDb);
        validateVersion(customerVersion, customerDb);
        contactRepository.deleteById(contactId);

//        final var contactDb = customerDb.getContacts().stream()
//            .filter(contact -> contact.getId().equals(contactId))
//            .findFirst()
//            .orElseThrow(() -> new NotFoundException(contactId));


        customerDb.getContactIds()
            .stream()
            .filter(contactIdDb -> contactIdDb.equals(contactId))
            .toList()
            .forEach(contactIdDb -> customerDb.getContactIds().remove(contactIdDb));
        customerRepository.save(customerDb);

        return true;
    }

    public void updatePassword(String newPassword, CustomUserDetails user) {
        log.debug("updatePassword: newPassword={}", newPassword);
        if (!checkPassword(newPassword)) {
            throw new PasswordInvalidException(newPassword);
        }
        keycloakService.updatePassword(newPassword, user.getJwt());
    }

    public void deleteById(final UUID id, final int version, final CustomUserDetails user) {
        //log.debug("deleteById: id={}, version={}, user={}", id, version, user);
        final var customerDb = customerRepository.findById(id).orElseThrow(NotFoundException::new);
        validateVersion(version, customerDb);
        final var roles = user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(str -> str.substring(RoleType.ROLE_PREFIX.length()))
            .map(RoleType::valueOf)
            .toList();

        if (!roles.contains(ADMIN)) {
            throw new AccessForbiddenException(user.getUsername(), roles);
        }

        // Sicherstellen, dass ContactIds nie `null` ist
        if (customerDb.getContactIds() == null) {
            customerDb.setContactIds(new ArrayList<>());
        }

        final var existingContacts = customerDb.getContactIds().stream()
            .map(contactRepository::findById)  // `Optional<Contact>` wird erzeugt
            .flatMap(Optional::stream)         // Entfernt `Optional.empty()`
            .toList();      // Liste von `Contact` Objekten

        existingContacts.forEach(existingContact -> contactRepository.deleteById(existingContact.getId()));

        keycloakService.delete(user.getToken(), customerDb.getUsername());
        customerRepository.delete(customerDb);
    }

    @SuppressWarnings("ReturnCount")
    private Boolean checkPassword(final CharSequence password) {
        if (password.length() < MIN_LENGTH) {
            return false;
        }
        if (!UPPERCASE.matcher(password).matches()) {
            return false;
        }
        if (!LOWERCASE.matcher(password).matches()) {
            return false;
        }
        if (!NUMBERS.matcher(password).matches()) {
            return false;
        }
        return SYMBOLS.matcher(password).matches();
    }
}
