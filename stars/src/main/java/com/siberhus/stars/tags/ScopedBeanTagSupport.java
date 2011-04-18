package com.siberhus.stars.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class ScopedBeanTagSupport implements Tag{
	
	private PageContext pageContext;
	
	private Tag parentTag;
	
	private String id;
	
	private int scope = PageContext.PAGE_SCOPE;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		if("page".equalsIgnoreCase(scope)){
			this.scope = PageContext.PAGE_SCOPE;
		}else if("request".equalsIgnoreCase(scope)){
			this.scope = PageContext.REQUEST_SCOPE;
		}else if("session".equalsIgnoreCase(scope)){
			this.scope = PageContext.SESSION_SCOPE;
		}else if("application".equalsIgnoreCase(scope)){
			this.scope = PageContext.APPLICATION_SCOPE;
		}else{
			throw new IllegalArgumentException("Unknown scope: "+scope);
		}
	}
	
	
	@Override
	public Tag getParent() {
		return parentTag;
	}

	@Override
	public void setParent(Tag tag) {
		this.parentTag = tag;
	}
	
	public PageContext getPageContext(){
		return pageContext;
	}
	
	@Override
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}
	
	@Override
	public int doStartTag() throws JspException {
		return SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	@Override
	public void release() {
		this.id = null;
		this.scope = PageContext.PAGE_SCOPE;
	}
	
	protected Object getBean(){
		return getPageContext().getAttribute(id, scope);
	}
	
	protected void setBean(Object bean){
		getPageContext().setAttribute(id, bean, scope);
	}
	
}
