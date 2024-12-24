package vn.khangktn.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    Meta meta;
    Object result;

    @Getter @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        private int totalPage;
        private long totalItem;

        public void setPage(int page) {
            this.page = page + 1;
        }
    }
}
