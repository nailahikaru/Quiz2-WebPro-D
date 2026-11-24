<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Attendance List" theme="app">
  <div class="container pb-5 my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="mb-0 fw-bold">Attendance List</h2>
        <p class="text-muted mb-0">Select an event to mark or view attendance.</p>
      </div>
    </div>

    <t:alerts/>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-4">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>No</th>
                <th>Event Name</th>
                <th>Date</th>
                <th>Status</th>
                <th class="text-center">Action</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="s" items="${schedules}" varStatus="st">
                <tr>
                  <td>${st.count}</td>
                  <td><strong><c:out value="${s.eventName}"/></strong></td>
                  <td>${u:date(s.eventDate, 'dd MMM yyyy')}</td>
                  <td>
                    <c:choose>
                      <c:when test="${s.upcoming}"><span class="badge bg-success">Upcoming</span></c:when>
                      <c:otherwise><span class="badge bg-secondary">Completed</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-center">
                    <a href="${ctx}/presence/view?id=${s.id}" class="btn btn-sm btn-primary">
                      <i class="bi bi-person-check"></i> View / Mark Attendance
                    </a>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty schedules}">
                <tr>
                  <td colspan="5" class="text-center text-muted py-5">
                    <i class="bi bi-calendar-x fs-1 d-block mb-3"></i>
                    No schedules available.
                  </td>
                </tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</t:layout>
