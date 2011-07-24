<%@ include file="/bugzooky/taglibs.jsp" %>

<div id="imageHeader">
    <table style="padding: 5px; margin: 0px; width: 100%;">
        <tr>
            <td id="pageHeader">Bugzooky: Stars & Stripes demo application</td>
            <td id="loginInfo">
                <c:if test="${not empty user}">
                    Welcome: ${user.firstName} ${user.lastName}
                    |
                </c:if>
                <stripes:link href="/action/bugzooky/authc/logout">Logout</stripes:link>
            </td>
        </tr>
    </table>
    <div id="navLinks">
        <stripes:link href="/action/bugzooky/multiBug">Bug List</stripes:link>
        <stripes:link href="/action/bugzooky/singleBug" event="create">Add Bug</stripes:link>
        <stripes:link href="/action/bugzooky/multiBug" event="create">Bulk Add</stripes:link>
        <stripes:link href="/action/bugzooky/administerPeople">Administer</stripes:link>
    </div>
</div>