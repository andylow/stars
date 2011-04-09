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
package com.siberhus.stars.core;

import java.io.Serializable;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 
 * @author hussachai
 *
 */
public class SessionScopeBeanWrapper implements HttpSessionListener, 
	HttpSessionActivationListener, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Object bean;
	
	public SessionScopeBeanWrapper(Object bean){
		this.bean = bean;
	}
	
	public Object getBean(){
		return bean;
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
//		if(bean instanceof IBeanLifecycle){
//			((IBeanLifecycle)bean).beforeDestroy();
//		}
	}
	
	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
		
	}
	
}
