package pods.project.accountservice.controllers;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping
    public String hello(){
        return "Hello From Account Service!";
    }
    @RequestMapping("/check")
    public String what(){
        return "I am Account-Service running at port 8080";
    }
}
