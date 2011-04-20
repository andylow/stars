<%@page import="javax.sql.DataSource"%>
<%@page import="javax.jms.ConnectionFactory"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="javax.naming.*" %>
<%@page import="net.sourceforge.stripes.examples.bugzooky.biz.*" %>
<%!
/**
* Recursively exhaust the JNDI tree
*/
private static final void listContext(JspWriter writer, Context ctx, String indent)throws Exception {
	try {
	   NamingEnumeration list = ctx.listBindings("");
	   while (list.hasMore()) {
	       Binding item = (Binding) list.next();
	       String className = item.getClassName();
	       String name = item.getName();
	       writer.println(indent + className + "&nbsp;&nbsp;" + name+"<br/>");
	       Object o = item.getObject();
	       if (o instanceof javax.naming.Context) {
				listContext(writer, (Context) o, indent + "&nbsp;&nbsp;");
	       }
	   }
	} catch (NamingException e) {
		e.printStackTrace();
	}
}
%>
<%
	Properties props = new Properties();
	props.put("java.naming.factory.initial","org.apache.openejb.client.LocalInitialContextFactory");
	props.put("bugzookyDs","new://Resource?type=DataSource");
	props.put("bugzookyDs.JdbcDriver","org.hsqldb.jdbcDriver");
	props.put("bugzookyDs.JdbcUrl","jdbc:hsqldb:mem:bugzooky");
	Context ctx = new InitialContext(props);
	
	listContext(out, ctx, "");
	
	PersonManager pm = (PersonManager)ctx.lookup("net.sourceforge.stripes.examples.bugzooky.biz.PersonManager");
	out.println(pm.getAllPeople()+"<br/>");
	
	ctx = new InitialContext();
	out.println(ctx.lookup("jdbc/bugzookyDs"));
	
	
	
%>