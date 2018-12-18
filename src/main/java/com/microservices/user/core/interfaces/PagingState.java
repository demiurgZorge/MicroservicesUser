package com.microservices.user.core.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Пагинация
 */
public class PagingState {
    
	public static final int  MAX_PAGE_SIZE = 100;

	/**
	 * Текущая позиция
	 */
    private Integer currentPosition;
	/**
	 * Размер страницы
	 */
    private Integer pageSize;
     
    public PagingState() {
    	construct(null, null);
    }
    
    public PagingState(Integer position, Integer size) {
		construct(position, size);
    }
    
    private void construct(Integer position, Integer size) {
    	if(position == null) {
			currentPosition = 0;
		}
		else {
			currentPosition = position;
		}
		
		if(size == null) {
			pageSize = MAX_PAGE_SIZE;
		}
		else {
			pageSize = size;
		}
    }
    
    public void setCurrentPosition(int currentPosition){
        this.currentPosition = currentPosition;
    }
    
    public int getCurrentPosition() {
        return currentPosition;
    }
    
    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }
    
    public int getPageSize(){
        return pageSize;
    }
    
    public <T> List<T> project(List<T> list) {
    	if (list.size() < currentPosition) {
    		return new ArrayList<>();
    	}
    	else if (list.size() <= (currentPosition + pageSize)) {
    		return list.subList(currentPosition, list.size());
    	}
    	else {
    		return list.subList(currentPosition, currentPosition + pageSize);
    	}
    }
}
