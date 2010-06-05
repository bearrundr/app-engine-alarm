<%@page import="org.json.JSONObject"%>
<%@ page language="java" contentType="text/json-comment-filtered" pageEncoding="ISO-8859-1"%>
<%@ page import="org.json.JSONObject" %>
<jsp:useBean id="root" class="org.json.JSONObject" scope="request"></jsp:useBean>
<%-- this is a silly jsp will only be here for debug, should just be a servlet --%>   
<%=root%> 