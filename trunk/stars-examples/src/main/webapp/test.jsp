<%@page import="net.sourceforge.stripes.controller.*" %>
<%@page import="net.sourceforge.stripes.exception.*" %>


<%
	ActionResolver ar = StripesFilter.getConfiguration().getActionResolver();
	try{
		Class a = ar.getActionBeanType("/action/bugzooky/multiBug/index");
		out.println(a);
	}catch(UrlBindingConflictException e){
		out.println(e.getMatches());
	}
%>