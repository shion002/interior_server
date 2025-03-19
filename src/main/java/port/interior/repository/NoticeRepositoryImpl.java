package port.interior.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import port.interior.entity.Notice;

import java.util.List;

import static port.interior.entity.QImage.image;
import static port.interior.entity.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Notice> findAllWithImages() {

        return queryFactory
                .selectFrom(notice)
                .leftJoin(notice.image, image).fetchJoin()
                .fetch();
    }

    @Override
    public List<String> getImageUrlsByPostId(Long postId) {
        return queryFactory
                .select(image.imageUrl)
                .from(image)
                .where(image.notice.id.eq(postId))
                .fetch();
    }

    @Override
    public List<Notice> findAllWithImagesSorted(String sortBy) {
        NoticeSort noticeSort = NoticeSort.fromString(sortBy);

        return queryFactory
                .selectFrom(notice)
                .leftJoin(notice.image, image).fetchJoin()
                .orderBy(getSortOrder(noticeSort))
                .fetch();
    }

    private static OrderSpecifier<?> getSortOrder(NoticeSort noticeSort) {
        return switch (noticeSort) {
            case OLDEST -> notice.createDate.asc();
            case NAME -> notice.title.asc();
            default -> notice.createDate.desc();
        };
    }
}
