<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<form action="home" method="post">

<table>

<tr>
<td> ID </td><td> <input type="text" name="id"> </td> 
<td> Name </td><td> <input type="text" name="name"> </td> 
<td>Location</td><td> <input type="text" name="Address.location"> </td> 
<td>City</td><td> <input type="text" name="Address.city"> </td> 
<td>State</td><td> <input type="text" name="Address.state"> </td>
</tr>

<tr> 
<td> File to upload </td> 
<td colspan="4"> <input type="file" name="file"> </td> 
</tr>

<tr>
<td colspan="2"><input type="submit" value="login"></td>
</tr>


</table>

</form>

</body>
</html>