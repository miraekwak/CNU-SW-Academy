package org.prgrms.kdt.customer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerService customerService;

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
