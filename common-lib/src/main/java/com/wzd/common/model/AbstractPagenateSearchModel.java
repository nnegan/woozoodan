package com.wzd.common.model;

import java.io.Serializable;

public abstract class AbstractPagenateSearchModel implements Serializable {
    protected Integer 	pageNo;
    protected Integer 	rowsPerPage;

    public Integer getPageNo() {
        return pageNo;
    }
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
    public Integer getRowsPerPage() {
        return rowsPerPage;
    }
    public void setRowsPerPage(Integer rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public Long getStartIndex() {
        if (this.pageNo == null || this.rowsPerPage == null) {
            return null;
        }
        return Long.valueOf(((long)this.pageNo-1) * this.rowsPerPage);
    }
}

