<html>
<body>

	<c:if test="${not empty errCode}">
		<h1>${errCode} : System Error</h1>
	</c:if>
	

	<c:if test="${not empty errMsg}">
		<h2>${errMsg}</h2>
	</c:if>
	
</body>
</html>