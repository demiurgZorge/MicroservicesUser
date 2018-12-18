package com.microservices.user.core.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Состояние сортировки
 */
public class SortState {

    public static enum SortType {
		ASCENDING("ASCENDING"), DESCENDING("DESCENDING");
		 private final String text;
	    
	    private SortType(final String text) {
	        this.text = text;
	    }

	    @Override
	    public String toString() {
	        return text;
	    }
	}

    /**
     * Наименование сортировки
     */
    private String   sortField;
    /**
     * Тип сортировки
     */
    private SortType sortType;

    public SortState() {
        sortField = null;
        sortType = null;
    }

    public SortState(String field, SortType type) {
        sortField = field;
        sortType = type;
    }

    public String getSortField() {
        return sortField;
    }

    public SortType getSortType() {
        return sortType;
    }

    private static SortState create(String name, String type) {
        try {
            SortType t = SortType.valueOf(type);
            return new SortState(name, t);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static SortState create(SortEnum name, SortType type) {
        return SortState.create(name.toString(), type.toString());
    }
    
    @JsonIgnore
    public boolean isAscending() {
        return SortType.ASCENDING.equals(this.sortType);
    }
    
    public boolean sortFieldIsEqualsTo(String name) {
        return sortField.equals(name);
    }
    
    public boolean sortFieldIsEqualsTo(SortEnum sort) {
        return sortField.equals(sort.toString());
    }
}
