package port.interior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import port.interior.entity.Image;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private String name;
    private String imageUrl;
    private int size;


    public ImageDto(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
