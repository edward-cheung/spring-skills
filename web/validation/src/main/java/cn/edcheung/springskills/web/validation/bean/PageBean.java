package cn.edcheung.springskills.web.validation.bean;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页bean封装
 */
@SuppressWarnings("unused")
public class PageBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     **/
    private int pageNum = 1;
    /**
     * 每页的数量
     **/
    private int pageSize = 10;
    /**
     * 总记录数
     **/
    private long total;
    /**
     * 总页数
     **/
    private int pages;
    /**
     * 结果集
     **/
    private List<T> list;

    public PageBean() {
    }

    public PageBean(int pageNum, int pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 包装Page对象
     *
     * @param list 分页数据
     */
    public PageBean(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.pages = page.getPages();
            this.list = page;
            this.total = page.getTotal();
        } else if (list instanceof ArrayList) {
            this.pageSize = list.size();
            this.pages = 1;
            this.list = list;
            this.total = list.size();
        }
    }

    /**
     * 包装Page对象
     *
     * @param list     分页数据
     * @param pageNum  页码
     * @param pageSize 分页大小
     */
    public PageBean(List<T> list, int pageNum, int pageSize) {
        this(list);
        if (pageSize > 0) {
            pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
        } else {
            pages = 0;
        }
        this.pageNum = pageNum;
    }

    /**
     * 包装Page对象
     *
     * @param list     分页数据
     * @param pageNum  页码
     * @param pageSize 分页大小
     * @param totalNum 总记录数
     */
    public PageBean(List<T> list, int pageNum, int pageSize, long totalNum) {
        this(list);
        if (pageSize > 0) {
            this.total = totalNum;
            pages = (int) (total / pageSize + ((total % pageSize == 0) ? 0 : 1));
        } else {
            pages = 0;
        }
        this.pageNum = pageNum;
    }

    /**
     * 包装Page对象
     *
     * @param list 分页数据
     */
    public PageBean(List<T> list, Page<T> page) {
        this.pageSize = page.getPageSize();
        this.pageNum = page.getPageNum();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.list = list;
    }

    public PageBean(com.github.pagehelper.PageInfo<T> pageInfo) {
        this.total = pageInfo.getTotal();
        this.pageSize = pageInfo.getPageSize();
        this.pages = pageInfo.getPages();
        this.pageNum = pageInfo.getPageNum();
        this.list = pageInfo.getList();
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}


