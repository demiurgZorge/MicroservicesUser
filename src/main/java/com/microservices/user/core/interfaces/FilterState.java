package com.microservices.user.core.interfaces;

/**
 * Состояние фильтра
 */
public class FilterState {
	/**
	 * Наименование фильтра
	 */
    private String filterName;
	/**
	 * Значение фильтра
	 */
	private Object fieldValue;
   
    public FilterState() {
    	
    }
    
    public FilterState(String filterName, Object value) {
    	this.filterName = filterName;
    	this.fieldValue = value;
    }
    
    public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
    
	public Object getFieldValue() {
		return fieldValue;
	}
	
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}
	
}
