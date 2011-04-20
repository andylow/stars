<%@ include file="/bugzooky/taglibs.jsp" %>

<stripes:layout-render name="/bugzooky/layout/standard.jsp" title="Bulk Add/Edit Bugs">
    <stripes:layout-component name="contents">
    
    	<stars:service id="componentManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManagerImpl"/>
		<stars:service id="personManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl"/>
		
    	<stars:spring id="componentManager" name="componentManager"/>
       	<stars:spring id="personManager" name="personManager"/>
       	
        <stripes:form action="${actionBeanUrl }" focus="">
            <stripes:errors/>

            <table class="display">
                <tr>
                    <th>ID</th>
                    <th><stripes:label name="bugs.component.id"/></th>
                    <th><stripes:label name="bugs.owner.id"/></th>
                    <th><stripes:label name="bugs.priority"/></th>
                    <th><stripes:label name="bugs.shortDescription"/></th>
                    <th><stripes:label name="bugs.longDescription"/></th>
                </tr>

                <c:choose>
                    <c:when test="${not empty actionBean.bugs}">
                        <c:set var="list" value="${actionBean.bugs}" scope="page"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="list" value="<%= new Object[5] %>" scope="page"/>
                    </c:otherwise>
                </c:choose>
                <c:forEach items="${list}" var="bug" varStatus="loop">
                    <tr>
                        <td>
                            ${bug.id}
                            <stripes:hidden name="bugs[${loop.index}].id"/>
                            <stripes:hidden name="bugs[${loop.index}].openDate"/>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].component.id">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-collection collection="${componentManager.allComponents}"
                                                            label="name" value="id"/>
                            </stripes:select>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].owner.id">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-collection collection="${personManager.allPeople}"
                                                            label="username" value="id"/>
                            </stripes:select>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].priority">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-enumeration enum="net.sourceforge.stripes.examples.bugzooky.model.Priority"/>
                            </stripes:select>
                        </td>
                        <td><stripes:textarea name="bugs[${loop.index}].shortDescription"/></td>
                        <td><stripes:textarea name="bugs[${loop.index}].longDescription"/></td>
                    </tr>
                </c:forEach>
            </table>

            <div class="buttons">
                <stripes:submit name="save" value="Save"/>
            </div>
        </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>