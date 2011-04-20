<%@ include file="/bugzooky/taglibs.jsp" %>

<div id="imageHeader">
    <table style="padding: 5px; margin: 0px; width: 100%;">
        <tr>
            <td id="pageHeader">Bugzooky: Stars & Stripes demo application</td>
            <td id="loginInfo">
                <c:if test="${not empty user}">
                    Welcome: ${user.firstName} ${user.lastName}
                    |
                    <stripes:link href="/bugzooky/logout.action">Logout</stripes:link>
                </c:if>
            </td>
        </tr>
    </table>
    <div id="navLinks">
        <stripes:link href="/bugzooky/multiBug.action">Bug List</stripes:link>
        <stripes:link href="/bugzooky/singleBug.action" event="create">Add Bug</stripes:link>
        <stripes:link href="/bugzooky/multiBug.action" event="create">Bulk Add</stripes:link>
        <stripes:link href="/bugzooky/administerPeople.action">Administer</stripes:link>
    </div>
</div>