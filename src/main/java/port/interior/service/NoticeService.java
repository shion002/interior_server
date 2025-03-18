package port.interior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import port.interior.dto.ImageDto;
import port.interior.dto.NoticeDto;
import port.interior.dto.NoticeResponseDto;
import port.interior.entity.Admin;
import port.interior.entity.Image;
import port.interior.entity.Notice;
import port.interior.repository.AdminRepository;
import port.interior.repository.ImageRepository;
import port.interior.repository.NoticeRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    public NoticeDto upload(NoticeDto noticeDto){
        Admin admin = adminRepository.findById(noticeDto.getAdminId())
                .orElseThrow(() -> new IllegalArgumentException("관리자가 존재하지 않습니다"));
        noticeDto.setCreateDate(LocalDateTime.now());
        log.info("noticeDto={}",noticeDto);
        Notice notice = noticeDto.toEntity(admin);
        Notice saveNotice = noticeRepository.save(notice);
        noticeDto.setId(saveNotice.getId());
        return NoticeDto.fromEntity(saveNotice);
    }

    public void delete(Long noticeId, Long adminId){
        noticeRepository.findById(noticeId).filter(n -> n.getAdmin().getId().equals(adminId))
                .ifPresent(noticeRepository::delete);
    }

    public void clear(){
        imageRepository.deleteAll();
        noticeRepository.deleteAll();
    }

    public NoticeDto updateNotice(Long postId, NoticeDto noticeDto){
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());
        notice.setUpdateDate(LocalDateTime.now());

        updateNoticeImages(notice, noticeDto.getImages());

        Notice updatedNotice = noticeRepository.save(notice);
        return NoticeDto.fromEntity(updatedNotice);
    }

    private void updateNoticeImages(Notice notice, List<ImageDto> newImageDtos) {
        List<Image> existingImages = new ArrayList<>(notice.getImage()); // 기존 이미지
        List<String> newImageUrls = newImageDtos.stream().map(ImageDto::getImageUrl).toList();

        List<Image> imagesToRemove = existingImages.stream()
                .filter(image -> !newImageUrls.contains(image.getImageUrl()))
                .toList();

        for (Image image : imagesToRemove) {
            s3Service.deleteFile(image.getImageUrl());
            notice.getImage().remove(image);
        }

        List<Image> imagesToAdd = newImageDtos.stream()
                .filter(dto -> existingImages.stream().noneMatch(img -> img.getImageUrl().equals(dto.getImageUrl())))
                .map(dto -> new Image(dto.getName(), dto.getImageUrl(), dto.getSize(), notice))
                .toList();

        notice.getImage().addAll(imagesToAdd);
    }

    public void uploadNoticeImages(Long postId, List<ImageDto> imageDtos) {
        log.info("받은 이미지 DTO: {}", imageDtos);
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        log.info("기존 이미지 개수: {}", notice.getImage().size());

        List<Image> images = imageDtos.stream()
                .map(dto -> new Image(dto.getName(), dto.getImageUrl(),dto.getSize(), notice)) // 연관관계 설정
                .toList();

        notice.getImage().clear();
        notice.getImage().addAll(images); // 이미지 리스트 추가

        noticeRepository.save(notice); // 변경 사항 저장
    }

    public List<NoticeResponseDto> findAll(){

        List<Notice> notices = noticeRepository.findAllWithImages();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return notices.stream().map(notice ->
                new NoticeResponseDto(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getImage().stream()
                                .map(image -> new ImageDto(image.getName(), image.getImageUrl(), image.getSize()))
                                .collect(Collectors.toList()),
                        notice.getContent(),
                        notice.getCreateDate().format(formatter),
                        notice.getUpdateDate().format(formatter)
                )
        ).collect(Collectors.toList());
    }

    public Notice findById(Long postId) {
        return noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));
    }

    public void deleteImages(Long postId, List<String> imageUrls) {
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        List<Image> imagesToRemove = notice.getImage().stream()
                .filter(image -> imageUrls.contains(image.getImageUrl()))
                .toList();

        imagesToRemove.forEach(image -> {
            notice.getImage().remove(image);
            imageRepository.delete(image);
        });

        noticeRepository.save(notice);
    }

    public void deleteNotice(Long postId) {
        noticeRepository.deleteById(postId);
    }

    public List<String> getImageUrlsByPostId(Long postId) {
        return noticeRepository.getImageUrlsByPostId(postId);
    }
}
