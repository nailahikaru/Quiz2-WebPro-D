<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="showErrors" required="false" type="java.lang.Boolean" %>
<c:if test="${not empty success}">
  <div class="alert alert-success alert-dismissible fade show" role="alert">
    <i class="bi bi-check-circle-fill"></i> <c:out value="${success}"/>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
</c:if>
<c:if test="${(empty showErrors or showErrors) and not empty errors}">
  <div class="alert alert-danger">
    <ul class="mb-0">
      <c:forEach var="err" items="${errors}">
        <li><c:out value="${err.value}"/></li>
      </c:forEach>
    </ul>
  </div>
</c:if>
