package port.interior.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import port.interior.entity.Image;
import port.interior.entity.Notice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class NoticeResponseDto {

    private Long id;
    private String title;
    private List<ImageDto> images;
    private String content;
    private String createDate;
    private String updateDate;

    public NoticeResponseDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createDate = notice.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.updateDate = notice.getUpdateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        this.images = notice.getImage() != null
                ? notice.getImage().stream().map(image -> new ImageDto(image.getImageUrl()))
                .collect(Collectors.toList()) : new ArrayList<>();
    }
}
