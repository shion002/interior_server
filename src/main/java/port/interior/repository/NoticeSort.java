package port.interior.repository;

public enum NoticeSort {
    LATEST, OLDEST, NAME;

    public static NoticeSort fromString(String value){
        try {
            return NoticeSort.valueOf(value);
        } catch (IllegalArgumentException e){
            return LATEST;
        }
    }

}
