package com.wzd.common.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 페이징 조회에 대한 결과(List)와 페이징 메타를 포함하는 모델
 */
@SuppressWarnings("serial")
public class PagenatedListModel<T> extends CommonResponseModel<List<?>>
        implements Serializable {

    private int 	totalCount;
    private int		page;
    private int		pageSize;
    private List<T>	dataList;

    public PagenatedListModel() {
        this(new ArrayList<T>());
    }

    public PagenatedListModel(List<T> dataList) {
        this.dataList	= dataList;
    }

    /**
     * 전체 데이터 건수를 반환합니다. (가능할때만)
     * @return
     */
    public int getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    /**
     * 페이지 번호를 반환 합니다. (1 base)
     * @return
     */
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    /**
     * 페이지당 데이터 건수를 반환합니다.
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public String toString() {
        return "PageListModel [totalCount=" + totalCount + ", page=" + page + ", pageSize=" + pageSize
                + ", getDataList=" + dataList + "]";
    }
}
