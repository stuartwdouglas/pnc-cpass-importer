package io.quarkus.pnc.importer.rest;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class PaginationParameters {

    @QueryParam(value = SwaggerConstants.PAGE_INDEX_QUERY_PARAM)
    @DefaultValue(value = SwaggerConstants.PAGE_INDEX_DEFAULT_VALUE)
    protected int pageIndex;

    @QueryParam(value = SwaggerConstants.PAGE_SIZE_QUERY_PARAM)
    @DefaultValue(value = SwaggerConstants.PAGE_SIZE_DEFAULT_VALUE)
    protected int pageSize;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
