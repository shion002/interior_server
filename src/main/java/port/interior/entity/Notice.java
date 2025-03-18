package port.interior.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NOTICE")
@Getter @Setter
@NoArgsConstructor
public class Notice {

    @Id @GeneratedValue
    private Long id;

    private String title;
    private String content;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Image> image = new ArrayList<>();

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;


    public Notice(Long id, String title, String content, List<Image> image, LocalDateTime createDate, LocalDateTime updateDate, Admin admin) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.admin = admin;
    }
}
