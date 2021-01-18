package com.dfq.coeffi.cbs.customer.api;

import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.customer.entity.CustomerFamilyDetails;
import com.dfq.coeffi.cbs.customer.entity.CustomerIntroducersDetails;
import com.dfq.coeffi.cbs.customer.entity.CustomerNomineeDatails;
import com.dfq.coeffi.cbs.customer.service.CustomerService;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.document.FileStorageService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class CustomerApi extends BaseController {

    private final CustomerService customerService;
    private final ApplicationLogService applicationLogService;
    private final FileStorageService fileStorageService;
    private final MemberService memberService;
    private final UserService userService;

    @Autowired
    private CustomerApi(CustomerService customerService, final ApplicationLogService applicationLogService,
                        final FileStorageService fileStorageService, final MemberService memberService, final UserService userService) {
        this.customerService = customerService;
        this.applicationLogService = applicationLogService;
        this.fileStorageService = fileStorageService;
        this.memberService = memberService;
        this.userService = userService;
    }

    @GetMapping("/customer")
    public ResponseEntity<List<Customer>> getCustomers() {
        List<Customer> customers = customerService.customers();
        if (CollectionUtils.isEmpty(customers)) {
            throw new EntityNotFoundException("customers");
        }
        return new ResponseEntity<List<Customer>>(customers, HttpStatus.OK);
    }

    @PostMapping("/customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer, Principal principal) {

        NumberFormat numberFormat = memberService.getNumberFormatByType("Customer");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate application number");
        }

        String applicationNumber = numberFormat.getPrefix() + "-" + (numberFormat.getApplicationNumber() + 1);

        List<CustomerFamilyDetails> familyMemberDetails = customer.getFamilyMemberDetails();
        List<CustomerNomineeDatails> nomineeDatails = customer.getNomineeDatails();

        if (familyMemberDetails != null && familyMemberDetails.size() > 0) {
            for (int i = 0; i < familyMemberDetails.size(); i++) {
                familyMemberDetails.get(i).setCustomer(customer);
            }
        }
        if (nomineeDatails != null && nomineeDatails.size() > 0) {
            for (int i = 0; i < nomineeDatails.size(); i++) {
                nomineeDatails.get(i).setCustomer(customer);
            }
        }

        if (customer.getDocumentIds() != null) {
            List<Document> documents = new ArrayList<>();

            for (Long documentId : customer.getDocumentIds()) {
                Document document = fileStorageService.getDocument(documentId);

                if (document != null) {
                    documents.add(document);
                }
            }
            customer.setDocuments(documents);
        }

        CustomerIntroducersDetails customerIntroducersDetails = customer.getIntroducersDetails();

        if (customerIntroducersDetails != null && customerIntroducersDetails.getIntroducerType().equalsIgnoreCase("Member")) {

            if (customerIntroducersDetails.getMember() != null) {
                Optional<Member> memberObj = memberService.getMember(customerIntroducersDetails.getMember().getId());
                Member member = memberObj.get();
                customerIntroducersDetails.setMember(member);
                customer.setIntroducersDetails(customerIntroducersDetails);
            }
        }
        if (customerIntroducersDetails != null && customerIntroducersDetails.getIntroducerType().equalsIgnoreCase("User")) {

            if (customerIntroducersDetails.getUser() != null) {
                User user = userService.getUser(customerIntroducersDetails.getUser().getId());
                customerIntroducersDetails.setUser(user);
                customer.setIntroducersDetails(customerIntroducersDetails);
            }
        }

        customer.setFamilyMemberDetails(familyMemberDetails);
        customer.setNomineeDatails(nomineeDatails);
        customer.setApplicationNumber(applicationNumber);

        Customer persistedObject = customerService.saveCustomer(customer);

        if (persistedObject != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Customer application no. " + persistedObject.getId() + " submitted",
                    "CUSTOMER APPLICATION SUBMIT", loggedUser.getId());
            numberFormat.setApplicationNumber(numberFormat.getApplicationNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        }
        return new ResponseEntity(persistedObject, HttpStatus.OK);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomer(id);
        if (!customer.isPresent()) {
            log.warn("Unable to find customer with ID : {} not found", id);
            throw new EntityNotFoundException(Customer.class.getName());
        }
        return new ResponseEntity<>(customer.get(), HttpStatus.OK);
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable long id, Principal principal) {
        Optional<Customer> customer = customerService.getCustomer(id);
        if (!customer.isPresent()) {
            log.warn("Unable to deactivate customer with ID : {} not found", id);
            throw new EntityNotFoundException(Customer.class.getSimpleName());
        }
        customerService.deleteCustomer(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/customer/update-profile-pic/{id}/{documentCategoryId}")
    public ResponseEntity<Customer> updateProfilePic(@PathVariable Long id, @PathVariable long documentCategoryId, @RequestParam("file") MultipartFile file) {
        Optional<Customer> customerObj = customerService.getCustomer(id);
        if (!customerObj.isPresent()) {
            log.warn("Unable to find customer with ID : {} not found", id);
            throw new EntityNotFoundException(Customer.class.getName());
        }
        Customer customer = customerObj.get();
        List<Document> documents = new ArrayList<>();
        Document document = fileStorageService.storeFile(file, documentCategoryId);
        documents.add(document);
        customer.setDocuments(documents);
        customerService.saveCustomer(customer);
        return new ResponseEntity(customer, HttpStatus.OK);
    }

}