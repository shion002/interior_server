package port.interior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        noticeDto.setUpdateDate(LocalDateTime.now());
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

    public void updateNotice(Long postId, NoticeDto noticeDto){
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        notice.updateNoticeDate(noticeDto.getTitle(), noticeDto.getContent());

        updateNoticeImages(notice, noticeDto.getImages());

        Notice updatedNotice = noticeRepository.save(notice);
        NoticeDto.fromEntity(updatedNotice);
    }

    private void updateNoticeImages(Notice notice, List<ImageDto> newImageDtos) {
        List<Image> existingImages = new ArrayList<>(notice.getImage());

        // 1. 삭제할 이미지 찾기
        List<Image> toRemove = existingImages.stream()
                .filter(img -> newImageDtos.stream()
                        .noneMatch(dto -> dto.getImageUrl().equals(img.getImageUrl())))
                .toList();

        toRemove.forEach(img -> {
            s3Service.deleteFile(img.getImageUrl());
            notice.getImage().remove(img);
        });

        // 2. 추가할 이미지 찾기
        for (int i = 0; i < newImageDtos.size(); i++) {
            ImageDto dto = newImageDtos.get(i);

            Image existing = notice.getImage().stream()
                    .filter(img -> img.getImageUrl().equals(dto.getImageUrl()))
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                Image newImg = new Image(dto.getName(), dto.getImageUrl(), dto.getSize(), i, notice);
                notice.getImage().add(newImg);
            } else {
                // 순서 갱신
                existing.setOrderIndex(i);
            }
        }
    }

    public void uploadNoticeImages(Long postId, List<ImageDto> imageDtos) {
        log.info("받은 이미지 DTO: {}", imageDtos);
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));

        log.info("기존 이미지 개수: {}", notice.getImage().size());

        List<Image> images = imageDtos.stream()
                .map(dto -> new Image(dto.getName(), dto.getImageUrl(),dto.getSize(), dto.getOrderIndex() ,notice))
                .toList();

        notice.getImage().clear();
        notice.getImage().addAll(images);

        noticeRepository.save(notice);
    }

    public List<NoticeResponseDto> findAll(String sortBy){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return noticeRepository.findAllWithImagesSorted(sortBy).stream().map(notice ->
                getNoticeResponseDto(notice, formatter)
        ).collect(Collectors.toList());
    }

    private static NoticeResponseDto getNoticeResponseDto(Notice notice, DateTimeFormatter formatter) {
        return new NoticeResponseDto(
                notice.getId(),
                notice.getTitle(),
                notice.getImage().stream()
                        .map(image -> new ImageDto(image.getName(), image.getImageUrl(), image.getSize(), image.getOrderIndex()))
                        .collect(Collectors.toList()),
                notice.getContent(),
                notice.getCreateDate().format(formatter),
                notice.getUpdateDate().format(formatter)
        );
    }

    public NoticeResponseDto findById(Long postId) {
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다: " + postId));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return getNoticeResponseDto(notice, formatter);
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

    public Page<Notice> getPageNotice(int page, int size){
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
        Page<Notice> notices = noticeRepository.findAll(pageable); // 기본 페이징 처리

        // Lazy Loading으로 인해 images 조회 시 N+1 발생 가능 → Batch Fetching 사용 추천
        notices.getContent().forEach(notice -> notice.getImage().size());

        return notices;
    }
}
