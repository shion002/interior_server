package port.interior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import port.interior.entity.Image;

import java.util.List;

@Data
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    private List<ImageDto> images;
    private String content;
    private String createDate;
    private String updateDate;
}
