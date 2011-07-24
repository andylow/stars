package net.sourceforge.stripes.examples.bugzooky;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simplistic security filter for Bugzooky that ensures that the user is logged in
 * before allowing access to any secured pages.
 *
 * @author Tim Fennell
 */
public class SecurityFilter implements Filter {
	
	private Logger log = LoggerFactory.getLogger(SecurityFilter.class);
	
    private static Set<String> publicUrls = new HashSet<String>();

    static {
        publicUrls.add("/bugzooky/exit.jsp");
        publicUrls.add("/bugzooky/authc");
        publicUrls.add("/bugzooky/register");
        publicUrls.add("/bugzooky/viewResource");
    }
    
    /** Does nothing. */
    public void init(FilterConfig filterConfig) throws ServletException { }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getSession().getAttribute("user") != null) {
            filterChain.doFilter(request, response);
        }else if ( isPublicResource(request) ) {
            filterChain.doFilter(request, response);
        }else {
            // Redirect the user to the login page, noting where they were coming from
            String targetUrl = StringUtil.urlEncode(request.getPathInfo());
            
            response.sendRedirect(
                    request.getContextPath() + "/action/bugzooky/authc?targetUrl=/action" + targetUrl);
        }
    }
    
    /**
     * Method that checks the request to see if it is for a publicly accessible resource
     */
    protected boolean isPublicResource(HttpServletRequest request) {
        String resource = request.getServletPath();
        String pathInfo = request.getPathInfo();
        log.debug("servlet path: {}", resource);
        log.debug("path info: {}",pathInfo);
        if("/action".equals(resource)){
        	for(String publicUrl : publicUrls){
        		if(pathInfo.startsWith(publicUrl)){
        			return true;
        		}
        	}
        	return false;
        }else if(resource.endsWith(".jsp")){
        	for(String publicUrl : publicUrls){
        		if(resource.startsWith(publicUrl)){
        			return true;
        		}
        	}
        	return false;
        }
        return (!resource.endsWith(".jsp") && !resource.endsWith(".action"));
    }
    
    /** Does nothing. */
    public void destroy() { }
}
