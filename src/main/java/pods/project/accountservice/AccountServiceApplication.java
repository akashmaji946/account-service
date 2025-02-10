/*
* @author Akash Maji
* @email akashmaji@iisc.ac.in
* @description Account Service for managing users
* @intent PODS-2025 Course Project (Phase 1)
* */

package pods.project.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AccountServiceApplication {

    public static void main(String[] args) {
        /* Spring FW starts the application from here */
        SpringApplication.run(AccountServiceApplication.class, args);
    }

}
