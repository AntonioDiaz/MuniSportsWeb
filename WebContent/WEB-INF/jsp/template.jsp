<!DOCTYPE html>
<html lang="en">
<head>
	<%@ include file="/WEB-INF/jsp/include.jsp"%>
	<title><tiles:insertAttribute name="title" ignore="true" defaultValue="title" /></title>
</head>
<body>
	<div class="container">
		<nav class="navbar navbar-default" style="margin-bottom: 10px; margin-top: 10px;">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" 
							data-target="#navbar" aria-expanded="false" aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="/">
						<img src="/images/logo.png" class="img-rounded" width="180px">
					</a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav">
						<li><a href="/sports/list">Deportes</a></li>
						<li><a href="/categories/list">Categorias</a></li>
						<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Competiciones <span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="/competitions/list">Lista</a></li>
								<li><a href="/competitions/add">Nueva</a></li>
							</ul>
						</li>
						<sec:authorize access="hasRole('ROLE_ADMIN')">
							<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#">Usuarios <span class="caret"></span></a>
								<ul class="dropdown-menu">
									<li><a href="/users/list">Lista usuarios</a></li>
									<li><a href="/users/add">Nuevo usuario</a></li>
								</ul>
							</li>
						</sec:authorize>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li>
							<a href="<c:url value="j_spring_security_logout" />">
								<span class="glyphicon glyphicon-log-in"></span>&nbsp;<b><c:out value="${pageContext['request'].userPrincipal.name }"></c:out>&nbsp;</b>
							</a>
						</li>
					</ul>
				</div>
			</div>
		</nav>	
		<div class="jumbotron" style="padding: 10px 30px;height:100%; min-height: 550px; background:transparent !important; border: 1px solid #e7e7e7;">
			<h2 style="color: #0061a8">
				<tiles:insertAttribute name="page_title" ignore="true" defaultValue="title" />
			</h2>
			<hr>
			<tiles:insertAttribute name="body" />
		</div>
	</div>
</body>
</html>
