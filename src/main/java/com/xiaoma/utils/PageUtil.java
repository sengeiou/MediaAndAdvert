package com.xiaoma.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 注意所有序号从1开始.
 * @param <T> Page中记录的类型.
 * @author calvin
 */
public class PageUtil<T> {
    //-- 公共变量 --//
    private Integer id;

    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final String ASC = "asc";

    public static final String DESC = "desc";

    public static final int FIRST_PAGE = 1;

    //-- 分页参数 --//
    protected int pageNo = FIRST_PAGE;

    protected int pageSize = DEFAULT_PAGE_SIZE;

    private List<OrderBy> orderBys;

    protected String orderBy = null;

    protected String order = null;

    protected boolean autoCount = true;

    //-- 返回结果 --//
    protected List<T> items = Lists.newArrayList();

    protected long totalCount = -1;

    //-- 构造函数 --//
    public PageUtil() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PageUtil(int pageSize) {
        this.pageSize = pageSize > 10 ? pageSize : 10;
    }

    public PageUtil(int pageSize, int pageNo) {
        this.pageSize = pageSize > 10 ? pageSize : 10;
        this.pageNo = pageNo > 1 ? pageNo: 1;
    }

    public PageUtil(int count, List<T> list) {
        totalCount = count;
        this.items = list;
    }

    //-- 分页参数访问函数 --//

    /**
     * 获得当前页的页号,序号从1开始,默认为1.
     */
    public int getPageNo() {
        return pageNo;
    }

    public long[] getPageNos() {
        int totalPages = (int) getTotalPages();
        long[] pageNos = new long[totalPages];
        for (int i = 0; i < totalPages; i ++) {
            pageNos[i] = i+1;
        }
        return pageNos;
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(final int pageNo) {
        this.pageNo = pageNo;

        if (pageNo < 1) {
            this.pageNo = 1;
        }
    }

    public PageUtil<T> orderBy(String propertyName, String dir) {
        if (orderBys == null) {
            orderBys = new ArrayList<OrderBy>();
        }
        orderBys.add(new OrderBy(propertyName, dir));
        return this;
    }

    @JsonIgnore
    public String getOrderByCriterion() {

        if (orderBys == null || orderBys.size() == 0) {
            return "";
        }

        return "ORDER BY " + StringUtils.join(orderBys, ", ");
    }

    @JsonIgnore
    public String getLimitCriterion() {

        return "LIMIT " + (getFirst()-1) + "," + getPageSize();
    }

    public List<OrderBy> getOrderBys() {
        return orderBys;
    }

    public void setOrderBys(List<OrderBy> orderBys) {
        this.orderBys = orderBys;
    }

    /**
     * 返回Page对象自身的setPageNo函数,可用于连续设置。
     */
    public PageUtil<T> pageNo(final int thePageNo) {
        setPageNo(thePageNo);
        return this;
    }

    /**
     * 获得每页的记录数量, 默认为-1.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页的记录数量.
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 返回Page对象自身的setPageSize函数,可用于连续设置。
     */
    public PageUtil<T> pageSize(final int thePageSize) {
        setPageSize(thePageSize);
        return this;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
     */
    public int getFirst() {
        int first = ((pageNo - 1) * pageSize) + 1;
        return first == 0 ? 1 : first;
    }

    /**
     * 根据pageNo和pageSize计算当前页最后一条记录在总结果集中的位置,序号从1开始.
     */
    public int getLast() {
        int last = ((pageNo) * pageSize);
        return last > totalCount ? (int) totalCount : last;
    }

    /**
     * 获得排序字段,无默认值. 多个排序字段时用','分隔.
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * 设置排序字段,多个排序字段时用','分隔.
     */
    public void setOrderBy(final String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * 返回Page对象自身的setOrderBy函数,可用于连续设置。
     */
    public PageUtil<T> orderBy(final String theOrderBy) {
        setOrderBy(theOrderBy);
        return this;
    }

    /**
     * 获得排序方向, 无默认值.
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置排序方式向.
     *
     * @param order 可选值为desc或asc,多个排序字段时用','分隔.
     */
    public void setOrder(final String order) {
        String lowcaseOrder = StringUtils.lowerCase(order);

        //检查order字符串的合法值
        String[] orders = StringUtils.split(lowcaseOrder, ',');
        for (String orderStr : orders) {
            if (!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr)) {
                throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
            }
        }

        this.order = lowcaseOrder;
    }

    /**
     * 返回Page对象自身的setOrder函数,可用于连续设置。
     */
    public PageUtil<T> order(final String theOrder) {
        setOrder(theOrder);
        return this;
    }

    /**
     * 是否已设置排序字段,无默认值.
     */
    public boolean isOrderBySetted() {
        return (StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order));
    }

    /**
     * 获得查询对象时是否先自动执行count查询获取总记录数, 默认为false.
     */
    public boolean isAutoCount() {
        return autoCount;
    }

    /**
     * 设置查询对象时是否自动先执行count查询获取总记录数.
     */
    public void setAutoCount(final boolean autoCount) {
        this.autoCount = autoCount;
    }

    /**
     * 返回Page对象自身的setAutoCount函数,可用于连续设置。
     */
    public PageUtil<T> autoCount(final boolean theAutoCount) {
        setAutoCount(theAutoCount);
        return this;
    }

    //-- 访问查询结果函数 --//

    /**
     * 获得页内的记录列表.
     */
    public List<T> getItems() {
        if (items == null) {
            return new ArrayList<T>();
        }
        return items;
    }

    /**
     * 设置页内的记录列表.
     */
    public void setItems(final List<T> items) {
        this.items = items;
    }

    /**
     * 获得总记录数, 默认值为-1.
     */
    public long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总记录数.
     */
    public void setTotalCount(final long totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        if (totalCount < 0) {
            return -1;
        }

        long count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }
        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean isHasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始.
     * 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        if (isHasNext()) {
            return pageNo + 1;
        } else {
            return pageNo;
        }
    }

    /**
     * 是否还有上一页.
     */
    public boolean isHasPre() {
        return (pageNo - 1 >= 1);
    }
    /**
     * 取得上页的页号, 序号从1开始.
     * 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        if (isHasPre()) {
            return pageNo - 1;
        } else {
            return pageNo;
        }
    }

    public int getFirstPage() {
        return FIRST_PAGE;
    }

    public int getLastPage() {
        int totalPage = (int) (totalCount / pageSize);
        totalPage = totalCount % pageSize > 0 ? totalPage + 1 : totalPage;
        return totalPage == 0 ? 1 : totalPage;
    }

    public static class OrderBy {

        private String propertyName;

        private String direction;

        public OrderBy() {
        }

        public OrderBy(String propertyName, String direction) {
            this.propertyName = propertyName;
            this.direction = direction;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        @Override
        public String toString() {
            return propertyName + " " + direction;
        }
    }
}
