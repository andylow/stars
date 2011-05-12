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

import java.io.Serializable;

/**
 * @see com.siberhus.ngai.QTO
 * @author hussachai
 * @since 0.9
 */
public class SortKey implements Serializable {

	/**
	 * Sort direction
	 * 
	 * @author hussachai
	 * 
	 */
	public enum SortDir {
		ASC, DESC
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field to be sorted
	 */
	private String key;

	/**
	 * Sort direction for specified key
	 */
	private SortDir sortDir;

	public SortKey() {
	}

	public SortKey(String key, SortDir sortDir) {
		this.key = key;
		this.sortDir = sortDir;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortKey other = (SortKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SortDir getSortDir() {
		return sortDir;
	}
	
	public void setSortDir(SortDir sortDir) {
		this.sortDir = sortDir;
	}
	
	public String getSortDirString(){
		if(getSortDir()!=null){
			return getSortDir().name();
		}else{
			return "";
		}
	}
	
	@Override
	public String toString(){
		if(getKey()==null){
			return "";
		}else{
			return getKey()+" "+getSortDirString();
		}
	}
	
}
