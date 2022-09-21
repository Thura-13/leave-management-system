<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Leaves | Home</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx"
	crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/js/bootstrap.bundle.min.js"
	integrity="sha384-A3rJD856KowSb7dwlZdYEkO39Gagi7vIsF0jrRAoQmDKKtQBHUuLZ9AsSv4jD4Xa"
	crossorigin="anonymous"></script>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.9.1/font/bootstrap-icons.css">
<c:url var="commonCss" value="/resources/application.css"></c:url>
<link rel="stylesheet" href="${commonCss}" />

</head>
<body>

	<c:import url="/jsp/include/navbar.jsp">
		<c:param name="view" value="students"></c:param>
	</c:import>

	<div class="container">
		<h3 class="my-4">Student List</h3>

		<!-- Search Form -->
		<form class="row">
			<div class="col-auto mb-4">
				<label class="form-label">Name</label> <input type="text" name="name"
					class="form-control" placeholder="Search Name"
					value="${param.name }"
					 />
			</div>
			<div class="col-auto mb-4">
				<label class="form-label">Phone</label> <input type="text" name="phone"
					class="form-control" placeholder="Search Phone" 
					value="${param.phone }"
					/>
			</div>
			<div class="col-auto mb-4">
				<label class="form-label">Email</label> <input type="text" name="email"
					class="form-control" placeholder="Search Email" 
					value="${param.email }"
					/>
			</div>
			<div class="col btn-wrapper">
				<button class="btn btn-outline-success">
					<i class="bi bi-search"></i> Search
				</button>
			</div>
		</form>

		<!-- Student Table for Search Result -->

		<c:choose>

			<c:when test="empty ${list }">
				<div class="alert alert-info">There is no data;</div>
			</c:when>

			<c:otherwise>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Id</th>
							<th>Name</th>
							<th>Phone</th>
							<th>Email</th>
							<th>Education</th>
							<th>Class</th>
						</tr>
					</thead>
					<c:forEach items="${list }" var="s">
						<tr>
							<td>${s.id}</td>
							<td>${s.name }</td>
							<td>${s.phone }</td>
							<td>${s.email }</td>
							<td>${s.education }</td>
							<td>${s.classCount }</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>


	</div>

</body>
</html>