package port.interior.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String imageUrl;
    private int size;

    @Setter
    @Column(name = "order_index")
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public Image(String name, String imageUrl, int size, int orderIndex, Notice notice) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.size = size;
        this.orderIndex = orderIndex;
        this.notice = notice;
    }

}
