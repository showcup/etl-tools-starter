package com.showcup.etl.rdbms;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description 通用分页结果对象
 * @author 封厂长
 * @date 2020-7-6
 */
@Data
@AllArgsConstructor
public class PageResult<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2024177159895254443L;
	
	//当前页
    private long pageNum;
    //每页的数量
    private long pageSize;
    
    //总记录数
    private long total;
    //总页数
    private long pages;
    //结果集
    private List<T> records;
    
    public PageResult(Long total,List<T> records) {
    	this.total = total;
    	this.records = records;
    }
}
