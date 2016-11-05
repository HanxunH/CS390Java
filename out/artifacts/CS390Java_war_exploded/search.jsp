<%--
  Created by IntelliJ IDEA.
  User: Curtis
  Date: 11/4/16
  Time: 10:27 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cs390.Crawler.*" %>
<%@ page import="java.util.*" %>


<html>
<head>
    <title>${keyword} - CS390Search</title>
</head>
<body>


<%
    // retrieve your list from the request, with casting
    List<searchResult> rs = (List<searchResult>)request.getAttribute("result_list");
    for(searchResult r : rs){
        out.println(r.getUrl());
    }
%>
</body>




</html>
