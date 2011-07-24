<%@ page import="net.sourceforge.stripes.examples.bugzooky.model.Status"%>
<%@ include file="/bugzooky/taglibs.jsp" %>

<stripes:layout-render name="/bugzooky/layout/standard.jsp" title="Unauthorized Request">
    <stripes:layout-component name="contents">
    	Error = ${LAST_EXCEPTION}
	</stripes:layout-component>
</stripes:layout-render>