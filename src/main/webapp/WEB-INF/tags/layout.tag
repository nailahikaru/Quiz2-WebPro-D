<%@ tag language="java" pageEncoding="UTF-8" body-content="scriptless" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="theme" required="false" %>
<%@ attribute name="scripts" fragment="true" required="false" %>
<c:set var="th" value="${empty theme ? 'home' : theme}"/>
<!doctype html>
<html lang="id">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${title}"/> — UKM Genshin</title>

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <c:choose>
    <c:when test="${th eq 'c'}">
      <link rel="stylesheet" href="${ctx}/css/fitur-c.css">
    </c:when>
    <c:when test="${th eq 'app'}">
      <link rel="stylesheet" href="${ctx}/assets/schedule.css">
      <style>
        body { padding-top: 80px; font-family: 'Poppins', sans-serif; }
      </style>
    </c:when>
    <c:when test="${th eq 'about'}">
      <link rel="stylesheet" href="${ctx}/css/profile-ukm.css">
    </c:when>
    <c:otherwise>
      <link rel="stylesheet" href="${ctx}/css/home.css">
    </c:otherwise>
  </c:choose>
  <link rel="stylesheet" href="${ctx}/css/navbar.css">
  <link rel="stylesheet" href="${ctx}/css/footer.css">
</head>
<body>
  <t:navbar/>

  <main>
    <c:choose>
      <c:when test="${th eq 'c'}">
        <div class="container"><jsp:doBody/></div>
      </c:when>
      <c:otherwise>
        <jsp:doBody/>
      </c:otherwise>
    </c:choose>
  </main>

  <t:footer/>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  <jsp:invoke fragment="scripts"/>
</body>
</html>
