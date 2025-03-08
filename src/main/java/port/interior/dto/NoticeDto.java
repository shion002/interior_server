package port.interior.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import port.interior.entity.Admin;
import port.interior.entity.Image;
import port.interior.entity.Notice;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Slf4j
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private List<ImageDto> images;
    private Long adminId;

    public Notice toEntity(Admin admin){
        Notice notice = new Notice(id, title, content, new ArrayList<>(), admin);

        log.info("NoticeDto.toEntity() 호출됨! 이미지 개수: {}", (images != null ? images.size() : 0));

        if (images != null && !images.isEmpty()) {
            for (ImageDto imgDto : images) {
                Image image = new Image(imgDto.getName(), imgDto.getImageUrl(), imgDto.getSize(), notice);
                notice.getImage().add(image);
                log.info("img={}", image.getImageUrl());
            }
        }
        return notice;
    }

    public NoticeDto(Long id, String title, String content, List<ImageDto> images, Long adminId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.images = images;
        this.adminId = adminId;
    }

    public static NoticeDto fromEntity(Notice notice){
        List<ImageDto> imageDtos = notice.getImage().stream()
                .map(image -> new ImageDto(image.getName(), image.getImageUrl(), image.getSize()))
                .toList();

        return new NoticeDto(notice.getId(), notice.getTitle(), notice.getContent(), imageDtos, notice.getAdmin().getId());
    }
}
