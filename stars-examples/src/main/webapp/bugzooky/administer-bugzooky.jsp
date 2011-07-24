<%@ include file="/bugzooky/taglibs.jsp" %>

<stripes:layout-render name="/bugzooky/layout/standard.jsp" title="Administer Bugzooky">
    <stripes:layout-component name="contents">
		
		<stars:service id="componentManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManagerImpl"/>
		<stars:service id="personManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl"/>
		
		<stars:spring id="componentManager" name="componentManager"/>
       	<stars:spring id="personManager" name="personManager"/>
       	
       	<stars:ejb id="componentManager" beanInterface="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManager"/>
		<stars:ejb id="personManager" beanInterface="net.sourceforge.stripes.examples.bugzooky.biz.PersonManager"/>
		
        <div class="subtitle">People</div>
        <stripes:form action="${pageContext.request.contextPath}/action/bugzooky/administerPeople">
            <stripes:errors/>

            <table class="display">
                <tr>
                    <th><img src="${pageContext.request.contextPath}/bugzooky/trash.png" title="Delete"/></th>
                    <th>ID</th>
                    <th>Username</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                </tr>
                <c:forEach items="${personManager.allPeople}" var="person" varStatus="loop">
                    <tr>
                        <td><stripes:checkbox name="deleteIds" value="${person.id}"
                                              onclick="handleCheckboxRangeSelection(this, event);"/></td>
                        <td>
                            ${person.id}
                            <stripes:hidden name="people[${loop.index}].id" value="${person.id}"/>
                        </td>
                        <td><stripes:text name="people[${loop.index}].username"  value="${person.username}"/></td>
                        <td><stripes:text name="people[${loop.index}].firstName" value="${person.firstName}"/></td>
                        <td><stripes:text name="people[${loop.index}].lastName"  value="${person.lastName}"/></td>
                        <td><stripes:text name="people[${loop.index}].email"     value="${person.email}"/></td>
                    </tr>
                    <c:set var="newIndex" value="${loop.index + 1}" scope="page"/>
                </c:forEach>
                <%-- And now, an empty row, to allow the adding of new users. --%>
                <tr>
                    <td></td>
                    <td></td>
                    <td><stripes:text name="people[${newIndex}].username"/></td>
                    <td><stripes:text name="people[${newIndex}].firstName"/></td>
                    <td><stripes:text name="people[${newIndex}].lastName"/></td>
                    <td><stripes:text name="people[${newIndex}].email"/></td>
                </tr>
            </table>

            <div class="buttons"><stripes:submit name="save" value="Save Changes"/></div>
        </stripes:form>

        <div class="subtitle">Components</div>
        <stripes:form action="${pageContext.request.contextPath}/action/bugzooky/administerComponents">
            <stripes:errors/>

            <table class="display" style="width: auto;">
                <tr>
                    <th><img src="${pageContext.request.contextPath}/bugzooky/trash.png" title="Delete"/></th>
                    <th>ID</th>
                    <th>Component Name</th>
                </tr>
                <c:forEach items="${componentManager.allComponents}" var="component" varStatus="loop">
                    <tr>
                        <td><stripes:checkbox name="deleteIds" value="${component.id}"
                                              onclick="handleCheckboxRangeSelection(this, event);"/></td>
                        <td>
                            ${component.id}
                            <stripes:hidden name="components[${loop.index}].id" value="${component.id}"/>
                        </td>
                        <td><stripes:text name="components[${loop.index}].name" value="${component.name}"/></td>
                    </tr>
                    <c:set var="newIndex" value="${loop.index + 1}" scope="page"/>
                </c:forEach>
                <%-- And now, an empty row, to allow adding new components. --%>
                <tr>
                    <td></td>
                    <td></td>
                    <td><stripes:text name="components[${newIndex}].name"/></td>
                </tr>
            </table>

            <div class="buttons"><stripes:submit name="save" value="Save Changes"/></div>
        </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>