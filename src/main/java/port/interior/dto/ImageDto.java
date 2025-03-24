package port.interior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import port.interior.entity.Image;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String name;
    private String imageUrl;
    private int size;

    public ImageDto(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public ImageDto(String name, String imageUrl, int size) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.size = size;
    }

    public ImageDto(Image image) {
    }
}
