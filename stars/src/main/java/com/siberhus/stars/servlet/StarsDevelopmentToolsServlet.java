package com.siberhus.stars.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.StreamingResolution;

import com.siberhus.stars.Environment;

public class StarsDevelopmentToolsServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	private static final String NAME = StarsDevelopmentToolsServlet.class.getName();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {}
	
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.service(request, response);
		String address = request.getRemoteAddr();
		if("127.0.0.1".equals(address) || "0:0:0:0:0:0:0:1".equals(address)){}
		else{
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		String command = request.getParameter("command");
		try{
			String contextPath = request.getServletContext().getContextPath();
			String servletPath = request.getServletPath();
			String uri = contextPath + servletPath;
			if("reloadCache".equals(command)){
				reloadCache(uri, request, response);
			}else{
				String html = "<HTML><BODY><H2>Stars Development Tools</H2>";
				Boolean error = (Boolean)getAttribute(request, "error");
				String message = (String)getAttribute(request, "message");
				String color = null;
				if(error!=null){
					if(error){
						color = "#ff0000";
					}else{
						color = "#339900";
					}
					html += "<SPAN STYLE=\"display:block;border:dotted;color:" + color +
							";border-width:thin;padding:5px;margin-bottom:10px;\">" + 
							message+"</SPAN>";
				}
				html +=	"<A HREF=\""+uri+"?command=reloadCache"+"\">Reload Cache</A><br/>" +
					"<BR/><HR/></BODY></HTML>";
				new StreamingResolution("text/html", html)
					.execute(request, response);
			}
		}catch(Exception e){
			throw new ServletException(e);
		}
	}
	
	public void reloadCache(String uri, HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException{
		String message = null;
		if(Environment.isDevelopment()){
			message = "The system has recieved reloading request.";
			Environment.requestReloading();
			setAttribute(request, "error", false);
		}else{
			message = "The system does not recieve reloading request " +
					"due to it's not running in development environment";
			setAttribute(request, "error", true);
		}
		setAttribute(request, "message", message);
		response.sendRedirect(uri);
	}
	
	private Object getAttribute(HttpServletRequest request, String name){
		name = NAME+"/"+name;
		Object value = request.getSession().getAttribute(name);
		request.getSession().removeAttribute(name);
		return value;
	}
	
	private void setAttribute(HttpServletRequest request, String name, Object value){
		request.getSession().setAttribute(NAME+"/"+name, value);
	}
	
}
