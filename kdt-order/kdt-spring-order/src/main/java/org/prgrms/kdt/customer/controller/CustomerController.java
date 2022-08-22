package org.prgrms.kdt.customer.controller;

import org.prgrms.kdt.customer.service.CustomerService;
import org.prgrms.kdt.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String viewCustomersPage(Model model) {
        var allCustomers = customerService.getAllCustomers();
        model.addAttribute("serverTime", LocalDateTime.now());
        model.addAttribute("customers", allCustomers);
        return "views/customers";
//        return new ModelAndView("views/customers",
//                Map.of("serverTime", LocalDateTime.now(),
//                        "customers", allCustomers));
    }

    @RequestMapping(value = "/api/v1/customers", method = RequestMethod.GET)
    @ResponseBody
    public List<Customer> findCustomers() {
        return customerService.getAllCustomers();
    }

//    @GetMapping("/customers/{customerId}")
//    public ResponseEntity<Customer> findCustomer(@PathVariable("customerId") UUID customerId) {
//        var maybeCustomer = customerService.getCustomer(customerId);
//        return maybeCustomer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }

    @PostMapping("/customers/{customerId}")
    public CustomerDto findCustomer(@PathVariable("customerId") UUID customerId, @RequestBody CustomerDto customer) {
        logger.info("Got customer save request {}", customer);
        var maybeCustomer = customerService.getCustomer(customerId);
        return customer;
    }

    @GetMapping("/customers/{customerId}")
    public String findCustomer(@PathVariable("customerId") UUID customerId, Model model) {
        var maybeCustomer = customerService.getCustomer(customerId);
        if (maybeCustomer.isPresent()){
            model.addAttribute("customer", maybeCustomer.get());
            return "views/customer-details";
        }
        else{
            return "views/404";
        }
    }

    @GetMapping("/customers/new")
    public String viewNewCustomerPage() {
        return "views/new-customers";
    }

    @PostMapping("/customers/new")
    public String addNewCustomer(CreateCustomerRequest createCustomerRequest) {
        customerService.createCustomer(createCustomerRequest.email(), createCustomerRequest.name());
        return "redirect:/customers";
    }

}
