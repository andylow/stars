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

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.displaytag.properties.SortOrderEnum;

/**
 * 
 * @author hussachai
 *
 */
public class DisplayTagUtil {
	
	/* Return one base page index , not zero base index */
	public static int getPageNumber(HttpServletRequest request) {
		int page = 0;
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if (name != null && name.startsWith("d-") && name.endsWith("-p")) {
				String pageValue = request.getParameter(name);
				if (pageValue != null) {
					page = Integer.parseInt(pageValue);
				}
			}
		}
		if(page==0){
			try{
				page = Integer.parseInt(request.getParameter("page"));
			}catch(NumberFormatException e){
				return 1;
			}
		}
		return page;
	}
	
	public static String getSortString(HttpServletRequest request){
		String sortParam = getSortCriterion(request);
		if(sortParam!=null){
			SortOrderEnum order = getSortOrder(request);
			if(order!=null){
				if(order==SortOrderEnum.ASCENDING){
					return sortParam+" asc";
				}else{
					return sortParam+" desc";
				}
			}
			return sortParam;
		}
		return null;
	}
	
	public static String getSortCriterion(HttpServletRequest request){
		String sortParam = request.getParameter("sort");
		return sortParam;
	}
	
	public static SortOrderEnum getInvertedSortOrder(HttpServletRequest request){
		SortOrderEnum order = getSortOrder(request);
		if(order==null) return null;
		if(order==SortOrderEnum.ASCENDING){
			return SortOrderEnum.DESCENDING;
		}
		return SortOrderEnum.ASCENDING;
	}
	
	public static SortOrderEnum getSortOrder(HttpServletRequest request){
		String directionParam = request.getParameter("dir");
		if(directionParam!=null){
			if("asc".equals(directionParam)){
				return SortOrderEnum.ASCENDING;
			}else{
				return SortOrderEnum.DESCENDING;
			}
		}
		return null;
	}
}
