package com.siberhus.stars.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.exception.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.WebAttributes;

import com.siberhus.org.stripesstuff.stripersist.Stripersist;
import com.siberhus.stars.ServiceProvider;
import com.siberhus.stars.stripes.StarsConfiguration;

public class CoreExceptionHandler implements ExceptionHandler {
	
	public static final String SEC_ACCESS_DENIED_PAGE = "Security.AccessDeniedPage";
	
	private StarsConfiguration configuration;
	private ServiceProvider serviceProvider;
	
	private String accessDeniedPage;
	
	@Override
	public void init(Configuration configuration) throws Exception {}
	
	public CoreExceptionHandler(Configuration configuration){
		this.configuration = (StarsConfiguration)configuration;
		this.serviceProvider = this.configuration.getServiceProvider();
		accessDeniedPage = configuration.getBootstrapPropertyResolver()
			.getProperty(SEC_ACCESS_DENIED_PAGE);
		if(accessDeniedPage!=null){
			if(accessDeniedPage.startsWith("/")){
				accessDeniedPage = configuration.getServletContext()
					.getContextPath() + accessDeniedPage;
			}
		}
	}
	
	@Override
	public void handle(Throwable throwable, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		if(ServiceProvider.STARS==serviceProvider){
			Stripersist.requestComplete(throwable);
		}
		
		//************** Handle Security Related Exception ***************//
		request.setAttribute("exception", throwable);
		
		//Check for the security related exception class existing before processing further.
		//TODO: if(securityImplementation==SPRING_SECURITY) SHIRO, JAVA_EE
		//Spring Security
		if(throwable instanceof AccessDeniedException){
			HttpSession session = request.getSession();
			session.setAttribute(WebAttributes.ACCESS_DENIED_403, throwable);
			if(accessDeniedPage!=null){
				response.sendRedirect(accessDeniedPage);
//				request.getRequestDispatcher(accessDeniedPage).forward(request, response);
			}else{
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied error (403)");
			}
		}
//		else if(throwable instanceof AuthorizationException){
//			
//		}
		throwable.printStackTrace();
	}
	
}
