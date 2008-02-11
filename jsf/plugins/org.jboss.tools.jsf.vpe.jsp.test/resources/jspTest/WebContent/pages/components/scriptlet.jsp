<?xml version="1.0" encoding="UTF-8"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
	<jsp:directive.page import="java.util.Date" />
	<jsp:directive.page contentType="application/xhtml+xml; charset=UTF-8" />
	<![CDATA[<?xml version="1.0" encoding="UTF-8"?>]]>
	<![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">]]>
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>Scriptlet Test</title>
	</head>
	<body>
	<h1>Scriptlet Test</h1>
	<jsp:scriptlet>
        out.print(new Date());
        </jsp:scriptlet>
	</body>
	</html>
</jsp:root>
