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
package com.siberhus.stars.ui.grid;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.Resolution;

/**
 * 
 * @author hussachai
 *
 */
public interface GridHandler {
	
	public int getPageNumber(HttpServletRequest request);
	
	public SortKey getSortKey(HttpServletRequest request);
	
	public Object getPaginatedList(HttpServletRequest request 
			,List<?> modelList , int fullListSize ,int pageNumber, int objectsPerPage);
	
	public Resolution getResolution(String pagePath);
	
}
