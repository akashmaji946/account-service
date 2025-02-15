package pods.project.accountservice.entities;

import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
    /* define the fields of the schema */
    @Id
    @Column(name = "id")
    Integer id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "email", unique = true)
    String email;

    @Column(name = "discount_availed")
    Boolean discount_availed;

    /* define the constructors */
    public User() {
        super();
    }

    /* define the getters and setters */
    public User(String name, String email, Boolean discount_availed) {
        super();
        this.name = name;
        this.email = email;
        this.discount_availed = discount_availed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDiscount_availed() {
        return discount_availed;
    }

    public void setDiscount_availed(Boolean discount_availed) {
        this.discount_availed = discount_availed;
    }
}
