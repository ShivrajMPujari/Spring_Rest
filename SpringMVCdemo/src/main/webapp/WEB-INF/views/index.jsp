<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<h3>${message}</h3>

<form action="home" method="post">

<table>

  <tr>
	<td>ID</td>
	<td><input name="id" type="text"/></td>
  </tr>
  <tr>
  <td> Name </td>
  <td> <input name="name" type="text"/></td>
  </tr>
  <tr>
  <td> MailId </td>
  <td> <input name="mailId" type="email"/></td>
  </tr>
  <tr>
  <td colspan="2"><input type="submit" value="login"> </td>
  </tr>
</table>


</form>

</body>
</html>