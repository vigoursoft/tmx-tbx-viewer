<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Upload File Response</title>
</head>
<body>
    <%-- Using JSP EL to get message attribute value from request scope --%>
    <h2>${requestScope.message}</h2>
    ${requestScope.errors}
    
  
</body>
</html>