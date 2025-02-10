package pods.project.accountservice.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pods.project.accountservice.entities.User;
import pods.project.accountservice.repositories.UserRepository;

import java.util.List;

@RestController
public class UserController {

    private static final Log log = LogFactory.getLog(UserController.class);
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteByUserId(@PathVariable Integer id) {
        List<User> users = userRepository.findByUserId(id);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userExistsNot(id));
        }
        try {
            userRepository.delete(users.get(0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userDeleteFailed(id));
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDeleteSucceded(id));
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(usersExistNot());
        }
        try {
            userRepository.deleteAll();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usersDeleteFailed());
        }
        return ResponseEntity.status(HttpStatus.OK).body(usersDeleteSucceded());
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
        return "User with email = " + email + " already exists";
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
