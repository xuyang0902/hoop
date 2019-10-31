package com.tongbanjie.console.web.model;

/**
 * 自定义分页组件
 *
 * @author yuren
 */
public class Page {

    /**
     * 当前页数
     */
    private Integer indexPage;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 每页条数
     */
    private Integer pageSize = 20;

    /**
     * 合计个数
     */
    private Integer totalSize;


    public static Page getInstance(Integer indexPage, Integer totalSize, Integer pageSize) {
        Page page = new Page();
        page.setIndexPage(indexPage);
        page.setTotalSize(totalSize);
        page.setPageSize(pageSize);
        return page;
    }

    public Integer getIndexPage() {
        return indexPage;
    }

    public void setIndexPage(Integer indexPage) {
        this.indexPage = indexPage;
    }

    public Integer getTotalPage() {
        return totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getOffset() {
        return (indexPage - 1) * pageSize;
    }
}