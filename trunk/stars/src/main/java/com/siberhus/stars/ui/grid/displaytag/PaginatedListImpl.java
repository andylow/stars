/* Copyright 2009 Hussachai Puripunpinyo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siberhus.stars.ui.grid.displaytag;
import java.util.List;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;

/**
 * 
 * @author hussachai
 *
 */
public class PaginatedListImpl implements PaginatedList{
	
	private List<?> list;
	private int fullListSize;
	private int objectsPerPage;
	private int pageNumber;
	private String searchId;
	private String sortCriterion;
	private SortOrderEnum sortDirection;
	
	public PaginatedListImpl(){
		
	}
	
	public int getFullListSize() {
		return fullListSize;
	}

	public PaginatedListImpl setFullListSize(int fullListSize) {
		this.fullListSize = fullListSize;
		return this;
	}

	public List<?> getList() {
		return list;
	}

	public PaginatedListImpl setList(List<?> list) {
		this.list = list;
		return this;
	}

	public int getObjectsPerPage() {
		return objectsPerPage;
	}

	public PaginatedListImpl setObjectsPerPage(int objectsPerPage) {
		this.objectsPerPage = objectsPerPage;
		return this;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public PaginatedListImpl setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public String getSearchId() {
		return searchId;
	}

	public PaginatedListImpl setSearchId(String searchId) {
		this.searchId = searchId;
		return this;
	}

	public String getSortCriterion() {
		return sortCriterion;
	}

	public PaginatedListImpl setSortCriterion(String sortCriterion) {
		this.sortCriterion = sortCriterion;
		return this;
	}

	public SortOrderEnum getSortDirection() {
		return sortDirection;
	}

	public PaginatedListImpl setSortDirection(SortOrderEnum sortDirection) {
		this.sortDirection = sortDirection;
		return this;
	}
	
}
