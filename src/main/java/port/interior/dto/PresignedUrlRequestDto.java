package port.interior.dto;
import lombok.Data;

@Data
public class PresignedUrlRequestDto {

    private Long postId;
    private String fileName;
}
