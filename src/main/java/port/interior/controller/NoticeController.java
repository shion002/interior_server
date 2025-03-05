package port.interior.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import port.interior.dto.ImageDto;
import port.interior.dto.NoticeDto;
import port.interior.dto.NoticeResponseDto;
import port.interior.dto.PresignedUrlRequestDto;
import port.interior.entity.Admin;
import port.interior.entity.Image;
import port.interior.entity.Notice;
import port.interior.service.AdminService;
import port.interior.service.NoticeService;
import port.interior.service.S3Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;
    private final S3Service s3Service;
    private final AdminService adminService;

    @PutMapping("/posting/{postId}/images")
    public ResponseEntity<?> uploadNoticeImages(@PathVariable Long postId,
                                                @RequestBody List<ImageDto> imageDtos){

        log.info("imageDto 전달받은 값 : {}", imageDtos);

        log.info("이미지 업로드 요청: postId={}, 이미지 개수={}", postId, imageDtos.size());

        noticeService.uploadNoticeImages(postId, imageDtos);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/posting/{postId}")
    public ResponseEntity<NoticeResponseDto> getNoticeById(@PathVariable Long postId) {
        Notice notice = noticeService.findById(postId);
        NoticeResponseDto responseDto = new NoticeResponseDto(
                notice.getId(),
                notice.getTitle(),
                notice.getImage().stream()
                        .map(image -> new ImageDto(image.getName(), image.getImageUrl(), image.getSize()))
                        .collect(Collectors.toList()),
                notice.getContent()
        );
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/posting/{postId}/update")
    public ResponseEntity<NoticeDto> updateNotice(@PathVariable Long postId,
                                                  @RequestBody NoticeDto noticeDto){
        NoticeDto updateNotice = noticeService.updateNotice(postId, noticeDto);
        return ResponseEntity.ok(noticeDto);
    }

    @DeleteMapping("/posting/{postId}/delete-notice")
    public ResponseEntity<?> deletePostWithImages(@PathVariable Long postId) {
        log.info("게시물 삭제 요청: postId={}", postId);

        try {
            // ✅ 1. 게시물에 포함된 이미지 URL 목록 가져오기
            List<String> imageUrls = noticeService.getImageUrlsByPostId(postId);

            log.info("삭제할 이미지 목록: {}", imageUrls);

            // ✅ 2. S3에서 이미지 삭제
            for (String imageUrl : imageUrls) {
                s3Service.deleteFile(imageUrl);
            }

            // ✅ 3. DB에서 이미지 삭제
            noticeService.deleteImages(postId, imageUrls);

            // ✅ 4. 게시물 삭제
            noticeService.deleteNotice(postId);

            return ResponseEntity.ok().body("게시물이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("게시물 삭제 중 오류 발생", e);
            return ResponseEntity.status(500).body("게시물 삭제 실패");
        }
    }

    @PostMapping("/posting/delete-file")
    public ResponseEntity<?> deleteFile(@RequestBody Map<String, String> requestData) {
        String fileUrl = requestData.get("fileUrl");

        log.info("fileUrl = {}", fileUrl);
        if (fileUrl == null || fileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("fileUrl is required");
        }

        try {
            s3Service.deleteFile(fileUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete file from S3");
        }
    }

    @PostMapping("/posting/{postId}/delete-images")
    public ResponseEntity<Void> deletePostImages(
            @PathVariable Long postId,
            @RequestBody List<String> imageUrls) {

        noticeService.deleteImages(postId, imageUrls);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posting/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestBody PresignedUrlRequestDto requestDto){
        log.info("postId:{}, fileName:{}", requestDto.getPostId(), requestDto.getFileName());

        if(requestDto.getPostId() == null || requestDto.getFileName() == null){
            log.error("postId 또는 fileName이 null입니다");
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
        }

        String presignedUrl = s3Service.generatePresignedUrl(requestDto.getPostId(), requestDto.getFileName());
        log.info("presignedUrl = {}", presignedUrl);

        return ResponseEntity.ok(Map.of(
                "url", presignedUrl
        ));
    }

    @PostMapping("/posting/upload")
    public ResponseEntity<Long> upload(@RequestBody NoticeDto noticeDto){
        Admin admin = adminService.currentAdmin();

        noticeDto.setAdminId(admin.getId());

        NoticeDto uploadNotice = noticeService.upload(noticeDto);
        return ResponseEntity.ok(uploadNotice.getId());
    }

    @GetMapping("/get/notice")
    public List<NoticeResponseDto> getNoticeAll(){
        adminService.deleteAdmin();
        adminService.save();
        List<Admin> byAll = adminService.findByAll();
        for (Admin admin : byAll) {
            log.info("관리자={} ", admin);
        }
        log.info("호출완료");
        return noticeService.findAll();
    }
}




















