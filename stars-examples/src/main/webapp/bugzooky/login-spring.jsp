<%@ include file="/bugzooky/taglibs.jsp" %>

<stripes:layout-render name="/bugzooky/layout/standard.jsp" title="Login">
    <stripes:layout-component name="contents">

        <table style="vertical-align: top;">
            <tr>
                <td style="width: 25%; vertical-align: top;">
                    <!-- Somewhat contrived example of using the errors tag 'action' attribute. -->
                    <c:if test="${not empty param.error}">
						<font color="red">
							Login error. <br />
							Reason : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
						</font>
					</c:if>
                    <form method="POST" action="<c:url value="/j_spring_security_check" />">
                        <table>
                            <tr>
                                <td style="font-weight: bold;"><stripes:label for="username"/>:</td>
                            </tr>
                            <tr>
                                <td><input type="text" name="j_username" /></td>
                            </tr>
                            <tr>
                                <td style="font-weight: bold;"><stripes:label for="password"/>:</td>
                            </tr>
                            <tr>
                                <td><input type="password" name="j_password" /></td>
                            </tr>
                            <tr>
                                <td style="text-align: center;">  
                                    <input type="submit" value="Login" />
                                </td>
                            </tr>
                        </table>
                    </form>
                </td>
                <td style="vertical-align: top;">
                    <c:choose>
                        <c:when test="${empty user}">
                            <div class="sectionTitle">Welcome</div>

                            <p>Welcome to Bugzooky, the Stars & Stripes demo application. If you haven't already
                            created an account, you will need to
                            <stripes:link href="/action/bugzooky/register">register</stripes:link>
                            in order to log in.</p>

                            <div class="sectionTitle">Intuitive development</div>

                            <p><a href="http://stripesframework.org">Stripes</a> is a relatively new framework
                            for developing web applications in Java.  It uses an action-oriented model
                            that will be familiar to anyone who has previously used Struts or anything
                            similar. Stripes is designed to be extremely easy to develop with - to be
                            intuitive to developers and to require the minimum amount of code and
                            configuration.</p>
							<p>
							<a href="http://www.siberhus.com/web/opensources/stars/">Stars</a> is a Stripes framework extension
							for developing full stack web application in Java. Although Stripes is an awesome easy web framework for Java,
							but it's not enough to develop a real world application that need persistence technology and service engine.
							Stars aims to integrate the existing great technology in persistence and enterprise servicing to Stripes framework
							such as JPA, Spring and EJB. By the way, Stars also provides its own persistence mechanism and service engine 
							in case of you just want to develop a small application. With Stars's development approach, you can change 
							the service provider easily in the future. 
							
							</p>
                            <p>As you try out the demo application you can use the links in the footer
                            to view the source of the current JSP, and of other files that are part of
                            the application.</p>
                        </c:when>

                        <c:otherwise>
                            <p>You are already logged in as '${user.username}'.  Logging in again will cause
                            you to  be logged out, and then re-logged in with the username and password
                            supplied.</p>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>

    </stripes:layout-component>
</stripes:layout-render>
