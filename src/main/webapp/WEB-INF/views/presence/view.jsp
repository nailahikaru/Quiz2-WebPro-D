<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Attendance: ${schedule.eventName}" theme="app">
  <div class="container pb-5 my-4">
    <t:alerts/>

    <div class="card border-0 shadow-sm">
      <div class="card-body p-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h3 class="mb-1 fw-bold">Attendance: <c:out value="${schedule.eventName}"/></h3>
            <p class="text-muted mb-0">
              <i class="bi bi-calendar"></i> ${u:date(schedule.eventDate, 'dd MMM yyyy')}
              | <i class="bi bi-geo-alt"></i> <c:out value="${schedule.eventLocation}"/>
            </p>
          </div>
          <div>
            <a href="${ctx}/presence" class="btn btn-outline-secondary me-2"><i class="bi bi-arrow-left"></i> Back</a>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#editAttendanceModal">
              <i class="bi bi-pencil-square"></i> Update Attendance
            </button>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-bordered table-hover">
            <thead class="table-light">
              <tr>
                <th>No</th>
                <th>Name</th>
                <th>Status</th>
                <th class="text-center">Reset</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="user" items="${users}" varStatus="st">
                <c:set var="status" value="${presences[user.id]}"/>
                <tr>
                  <td>${st.count}</td>
                  <td><c:out value="${user.name}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${status eq 'present'}"><span class="badge bg-success">Present</span></c:when>
                      <c:when test="${status eq 'leave'}"><span class="badge bg-warning text-dark">Leave</span></c:when>
                      <c:when test="${status eq 'alfa'}"><span class="badge bg-danger">Absent</span></c:when>
                      <c:otherwise><span class="badge bg-secondary">Not Marked</span></c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-center">
                    <c:if test="${not empty status}">
                      <form action="${ctx}/presence/reset" method="post" class="d-inline"
                            onsubmit="return confirm('Remove this attendance mark?');">
                        <t:csrf/>
                        <input type="hidden" name="sch_id" value="${schedule.id}">
                        <input type="hidden" name="user_id" value="${user.id}">
                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Remove mark">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <div class="modal fade" id="editAttendanceModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Update Attendance</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <form action="${ctx}/presence/save" method="post">
          <t:csrf/>
          <input type="hidden" name="sch_id" value="${schedule.id}">
          <div class="modal-body">
            <div class="table-responsive">
              <table class="table table-bordered">
                <thead class="table-light">
                  <tr>
                    <th>Name</th>
                    <th class="text-center">Status</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="user" items="${users}">
                    <c:set var="current" value="${empty presences[user.id] ? 'alfa' : presences[user.id]}"/>
                    <tr>
                      <td><c:out value="${user.name}"/></td>
                      <td class="text-center">
                        <div class="btn-group btn-group-sm">
                          <input type="radio" class="btn-check" name="status[${user.id}]" id="p_present_${user.id}"
                                 value="present" ${current eq 'present' ? 'checked' : ''}>
                          <label class="btn btn-outline-success" for="p_present_${user.id}">Present</label>

                          <input type="radio" class="btn-check" name="status[${user.id}]" id="p_leave_${user.id}"
                                 value="leave" ${current eq 'leave' ? 'checked' : ''}>
                          <label class="btn btn-outline-warning" for="p_leave_${user.id}">Leave</label>

                          <input type="radio" class="btn-check" name="status[${user.id}]" id="p_alfa_${user.id}"
                                 value="alfa" ${current eq 'alfa' ? 'checked' : ''}>
                          <label class="btn btn-outline-danger" for="p_alfa_${user.id}">Absent</label>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
            <button type="submit" class="btn btn-primary">Save Attendance</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</t:layout>
