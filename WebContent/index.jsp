<%@page import="java.net.InetAddress"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Hop Server</title>
</head>
<body>
	<h1>Hop Server Web Version</h1>
	This simple application makes requests through the list of servers<br>
	by calling the first one and sending it the remaining servers on the list<br>
	Each server shows its details and forwards the request for the next one.
	<br>
	<br>
	Write down the desired flow of server hops. 
	<br>
	Write in the form server:port (e.g.: 10.0.0.2:8080).
	<%
	   InetAddress localaddress = InetAddress.getLocalHost();
	%>
	<br> 
	I am <b><%= localaddress %></b>. <br>
	Please do not include me in the list (You could, but you know I am on).
	<br> 
	<br>
	<form  action="hop" method="post">
		<textarea rows="4" cols="50" name="serverlist"></textarea>
		<br>
		<br> Click to show request flow <input type="submit" value="ok"></input>
	</form>
</body>
</html>