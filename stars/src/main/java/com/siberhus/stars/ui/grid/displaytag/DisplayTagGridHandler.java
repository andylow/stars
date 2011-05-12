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

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import org.displaytag.properties.SortOrderEnum;

import com.siberhus.stars.ui.grid.GridHandler;
import com.siberhus.stars.ui.grid.SortKey;

public class DisplayTagGridHandler implements GridHandler {
	
	private static DisplayTagGridHandler instance;
	
	private DisplayTagGridHandler(){}
	
	public static synchronized DisplayTagGridHandler getInstance(){
		if(instance==null){
			instance = new DisplayTagGridHandler();
		}
		return instance;
	}
	
	@Override
	public int getPageNumber(HttpServletRequest request){
		int pageNumber = DisplayTagUtil.getPageNumber(request);
		return pageNumber;
	}
	
	@Override
	public SortKey getSortKey(HttpServletRequest request){
		String key = DisplayTagUtil.getSortCriterion(request);
		if(key==null){
			return null;
		}
		SortKey.SortDir sortDir = null;
		SortOrderEnum sortOrderEnum = DisplayTagUtil.getSortOrder(request);
		if(SortOrderEnum.ASCENDING==sortOrderEnum){
			sortDir = SortKey.SortDir.ASC;
		}else if(SortOrderEnum.DESCENDING==sortOrderEnum){
			sortDir = SortKey.SortDir.DESC;
		}
		return new SortKey(key, sortDir);
	}
	
	@Override
	public Object getPaginatedList(HttpServletRequest request
			,List<?> modelList , int fullListSize ,int pageNumber, int objectsPerPage){
		
		String sortCriterion = DisplayTagUtil.getSortCriterion(request);
		SortOrderEnum sortDirection = DisplayTagUtil.getSortOrder(request);
		
		PaginatedListImpl paginatedList = new PaginatedListImpl()
			.setList(modelList)
			.setFullListSize(fullListSize)
			.setObjectsPerPage(objectsPerPage)
			.setPageNumber(pageNumber)
			.setSortCriterion(sortCriterion)
			.setSortDirection(sortDirection);
		
		return paginatedList;
	}

	@Override
	public Resolution getResolution(String pagePath) {
		return new ForwardResolution(pagePath);
	}
	
	
}
