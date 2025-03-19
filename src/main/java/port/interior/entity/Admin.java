package port.interior.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Admin {

    @Id @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
