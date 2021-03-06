<%@ page import="net.sourceforge.stripes.examples.bugzooky.model.Status"%>
<%@ include file="/bugzooky/taglibs.jsp" %>

<stripes:layout-render name="/bugzooky/layout/standard.jsp" title="Add/Edit Bug">
    <stripes:layout-component name="contents">
        
        <stars:service id="componentManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManagerImpl"/>
		<stars:service id="personManager" serviceBean="net.sourceforge.stripes.examples.bugzooky.biz.PersonManagerImpl"/>
		
        <stars:spring id="componentManager" type="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManager"/>
       	<stars:spring id="personManager" name="personManager"/>
       	
       	<stars:ejb id="componentManager" beanInterface="net.sourceforge.stripes.examples.bugzooky.biz.ComponentManager"/>
		<stars:ejb id="personManager" beanInterface="net.sourceforge.stripes.examples.bugzooky.biz.PersonManager"/>
		
        <stripes:errors/>
		
        <stripes:form action="${actionBeanUrl }" focus="bug.shortDescription">
            <table class="leftRightForm">
                <tr>
                    <th>Bug ID:${bugManager }</th>
                    <td>
                        ${empty actionBean.bug.id ? "n/a" : actionBean.bug.id}
                        <stripes:hidden name="bug.id"/>
                    </td>
                </tr>
                <tr>
                    <th>Opened On:</th>
                    <td>
                    	<fmt:formatDate value="${actionBean.bug.openDate}" dateStyle="medium"/>
                    	<stripes:hidden name="bug.openDate"/>
					</td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.component.id"/>:</th>
                    <td>
                        <stripes:select name="bug.component.id">
                            <stripes:options-collection collection="${componentManager.allComponents}"
								label="name" value="id"/>
                        </stripes:select>
                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.owner.id"/>:</th>
                    <td>
                        <stripes:select name="bug.owner.id">
                            <stripes:options-collection collection="${personManager.allPeople}"
								label="username" value="id" sort="label"/>
                        </stripes:select>
                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.priority"/>:</th>
                    <td>
                        <stripes:select name="bug.priority" value="Medium">
                            <stripes:options-enumeration enum="net.sourceforge.stripes.examples.bugzooky.model.Priority"/>
                        </stripes:select>
                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.status"/>:</th>
                    <td>
                        <c:forEach var="status" items="<%=Status.values()%>">
                            <stripes:radio id="bug.status.${stripes:enumName(status)}"
                                           name="bug.status"
                                           value="${stripes:enumName(status)}"
                                           checked="New"/>
                            <stripes:label for="bug.status.${stripes:enumName(status)}">${stripes:enumName(status)}</stripes:label>
                        </c:forEach>

                    </td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.dueDate"/>:</th>
                    <td><stripes:text name="bug.dueDate" formatPattern="medium"/></td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.percentComplete"/>:</th>
                    <td><stripes:text name="bug.percentComplete" formatType="percentage"/></td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.shortDescription"/>:</th>
                    <td><stripes:text style="width: 500px;" name="bug.shortDescription"/></td>
                </tr>
                <tr>
                    <th><stripes:label for="bug.longDescription"/>:</th>
                    <td><stripes:textarea style="width:500px; height:3em;" name="bug.longDescription"/></td>
                </tr>
                <tr>
                    <th>Attachments:</th>
                    <td>
                        <c:forEach items="${actionBean.bug.attachments}" var="attachment" varStatus="loop">
                            ${attachment.name} (${attachment.size} bytes) -
                            <stripes:link href="${pageContext.request.contextPath }/action/bugzooky/downloadAttachment">
                                <stripes:param name="bugId" value="${actionBean.bug.id}"/>
                                <stripes:param name="attachmentIndex" value="${loop.index}"/>
                                <em>${attachment.preview}...</em>
                            </stripes:link><br/>
                        </c:forEach>

                        Add a new attachment: <stripes:file name="newAttachment"/>
                    </td>
                </tr>
            </table>

            <div class="buttons">
                <stripes:submit name="save" value="Save and Return"/>
                <stripes:submit name="saveAndAgain" value="Save and Add Another"/>
            </div>
        </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>