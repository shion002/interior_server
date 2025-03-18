package port.interior.repository;

import port.interior.entity.Notice;

import java.util.List;

public interface NoticeRepositoryCustom {
    List<Notice> findAllWithImages();

    List<String> getImageUrlsByPostId(Long postId);

    List<Notice> findAllWithImagesSorted(String sortBy);
}
