package pods.project.accountservice.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pods.project.accountservice.entities.User;
import pods.project.accountservice.repositories.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private static final Log log = LogFactory.getLog(UserController.class);
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.restTemplate = new RestTemplate();
        this.userRepository = userRepository;
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> findByUserId(@PathVariable Integer id) {
        List<User> users = userRepository.findByUserId(id);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userExistsNot(id));
        }
        return ResponseEntity.status(HttpStatus.OK).body(users.get(0));
    }

    @PostMapping("/users")
    public ResponseEntity<?> insertIntoUsers(@RequestBody User user) {
        Integer id = user.getId();
        List<User> users = userRepository.findByUserId(id);
        if (!users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userExists(users.get(0)));
        }
        user.setDiscount_availed(false);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userCreateFailed(user));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> request) {
        Integer id = Integer.parseInt(request.get("id").toString());
        List<User> users = userRepository.findByUserId(id);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userExistsNot(id));
        }
        User user = users.get(0);
        user.setDiscount_availed(true);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userCreateFailed(user));
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }



    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteByUserId(@PathVariable Integer id) {
        List<User> users = userRepository.findByUserId(id);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userExistsNot(id));
        }
        boolean walletExists = getWalletStatus(id);
        if (walletExists) {
             deleteWalletForUser(id);
        }
        // delete all orders for this user
        deleteOrdersForUser(id);
        try {
            userRepository.delete(users.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userDeleteFailed(id));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDeleteSucceded(id));
    }

    private void deleteOrdersForUser(Integer id) {
        String url = "http://localhost:8082/marketplace/users/" + id;
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, null);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("All Orders Deleted for User with id=" + id);
            }
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean deleteWalletForUser(Integer id) {
        String url = "http://localhost:8081/wallets/" + id;
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, null);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Wallet Deleted for User with id=" + id);
                return true;
            }
            return false;
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean getWalletStatus(Integer id) {

            String url = "http://localhost:8081/wallets/" + id;

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("userId", 123);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, null);

            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Wallet Exists");
                    return true;
                }
                return false;
            } catch (RestClientException e) {
                System.out.println(e.getMessage());
                return false;
            }
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(usersDeleteSucceded());
        }
        // delete all wallets
        deleteWallets();
        // delete all orders
        deleteOrders();

        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usersDeleteFailed());
        }
        return ResponseEntity.status(HttpStatus.OK).body(usersDeleteSucceded());
    }

    private void deleteOrders() {
        String url = "http://localhost:8082/marketplace/users";
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, null);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("All Orders Deleted");
            }
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteWallets() {
        String url = "http://localhost:8081/wallets";
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, null);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Wallets Deleted");
            }
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
        }
    }

    private Object usersDeleteSucceded() {
        return "Users deleted successfully";
    }

    private Object usersDeleteFailed() {
        return "Users delete failed";
    }

    private Object usersExistNot() {
        return "Users do NOT exist";
    }

    private Object userDeleteSucceded(Integer id) {
        return "User with id " + id + " was deleted";
    }

    private Object userDeleteFailed(Integer id) {
        return "User with id " + id + " was NOT deleted";
    }

    private Object userCreateFailed(User user) {
        String email = user.getEmail();
        return "User with email = " + email + " can't be  created";
    }

    private Object userExists(User user) {
        Integer id = user.getId();
        String name = user.getName();
        return "User with id = " + id + " and name = " + name + " already exists";
    }

    private Object userExistsNot(Integer id) {
        return "User with id = " + id + " does not exist";
    }

}
