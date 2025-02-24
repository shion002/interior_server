package port.interior.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String imageUrl;
    private int size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    public Image(String fileName, String imageUrl, int fileSize, Notice notice) {
        this.name = fileName;
        this.imageUrl = imageUrl;
        this.size = fileSize;
        this.notice = notice;
    }
}
