<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Event Schedule" theme="app">
  <jsp:attribute name="scripts">
    <script src="${ctx}/assets/schedule.js"></script>
    <script>
      // Isi modal "Edit Schedule" dengan data baris yang diklik
      document.querySelectorAll('.btn-edit-schedule').forEach(function (btn) {
        btn.addEventListener('click', function () {
          var f = document.getElementById('editScheduleForm');
          f.querySelector('[name=id]').value = btn.dataset.id;
          f.querySelector('[name=eventName]').value = btn.dataset.name;
          f.querySelector('[name=eventDate]').value = btn.dataset.date;
          f.querySelector('[name=eventTime]').value = btn.dataset.time;
          f.querySelector('[name=eventLocation]').value = btn.dataset.location;
          f.querySelector('[name=eventDesc]').value = btn.dataset.desc;
          new bootstrap.Modal(document.getElementById('editScheduleModal')).show();
        });
      });
    </script>
  </jsp:attribute>
  <jsp:body>
    <div class="container pb-5 my-4">

      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="mb-0 fw-bold">Event Schedule</h2>
          <p class="text-muted mb-0">List of UKM agendas and events</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addScheduleModal">
          <i class="bi bi-plus-lg"></i> Add Schedule
        </button>
      </div>

      <t:alerts/>

      <div class="card border-0 shadow-sm">
        <div class="card-body p-4">

          <div class="row mb-4">
            <div class="col-md-6">
              <div class="input-group">
                <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
                <input type="text" class="form-control border-start-0 ps-0" id="searchInput"
                       placeholder="Search events by name or location...">
              </div>
            </div>
          </div>

          <div class="table-responsive">
            <table class="table table-hover align-middle" id="scheduleTable">
              <thead class="table-light">
                <tr>
                  <th>No</th>
                  <th>Event Name</th>
                  <th>Date &amp; Time</th>
                  <th>Location</th>
                  <th>Status</th>
                  <th class="text-center">Actions</th>
                </tr>
              </thead>
              <tbody id="scheduleBody">
                <c:forEach var="s" items="${schedules}" varStatus="st">
                  <tr>
                    <td>${st.count}</td>
                    <td>
                      <strong><c:out value="${s.eventName}"/></strong>
                      <br><small class="text-muted">Created by: <c:out value="${empty s.creatorName ? 'Unknown' : s.creatorName}"/></small>
                    </td>
                    <td>
                      <i class="bi bi-clock text-muted"></i>
                      ${u:date(s.eventDate, 'dd MMM yyyy')}, ${u:date(s.eventTime, 'HH:mm')}
                    </td>
                    <td><i class="bi bi-geo-alt text-muted"></i> <c:out value="${s.eventLocation}"/></td>
                    <td>
                      <c:choose>
                        <c:when test="${s.upcoming}"><span class="badge bg-success">Upcoming</span></c:when>
                        <c:otherwise><span class="badge bg-secondary">Completed</span></c:otherwise>
                      </c:choose>
                    </td>
                    <td class="text-center">
                      <a href="${ctx}/presence/view?id=${s.id}" class="btn btn-sm btn-outline-info"
                         title="View / Mark Attendance"><i class="bi bi-person-check"></i></a>

                      <button type="button" class="btn btn-sm btn-outline-warning btn-edit-schedule" title="Edit"
                              data-id="${s.id}"
                              data-name="<c:out value='${s.eventName}'/>"
                              data-date="${u:iso(s.eventDate)}"
                              data-time="${u:date(s.eventTime, 'HH:mm')}"
                              data-location="<c:out value='${s.eventLocation}'/>"
                              data-desc="<c:out value='${s.eventDesc}'/>">
                        <i class="bi bi-pencil"></i>
                      </button>

                      <form action="${ctx}/schedule/delete" method="post" class="d-inline"
                            onsubmit="return confirm('Are you sure you want to delete this schedule?');">
                        <t:csrf/>
                        <input type="hidden" name="id" value="${s.id}">
                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty schedules}">
                  <tr>
                    <td colspan="6" class="text-center text-muted py-5">
                      <i class="bi bi-calendar-x fs-1 d-block mb-3"></i>
                      No schedules found. Click "Add Schedule" to create one.
                    </td>
                  </tr>
                </c:if>
              </tbody>
            </table>
          </div>

          <div id="noResults" class="text-center text-muted d-none mt-3">
            <i class="bi bi-search fs-4"></i>
            <p>No events found matching your search.</p>
          </div>

        </div>
      </div>
    </div>

    <%-- Modal tambah --%>
    <div class="modal fade" id="addScheduleModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add New Schedule</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form action="${ctx}/schedule/store" method="post">
            <t:csrf/>
            <div class="modal-body">
              <div class="mb-3">
                <label for="eventName" class="form-label">Event Name</label>
                <input type="text" class="form-control" id="eventName" name="eventName"
                       placeholder="e.g., Weekly Meeting" required>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label for="eventDate" class="form-label">Start Date</label>
                  <input type="date" class="form-control" id="eventDate" name="eventDate" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label for="eventTime" class="form-label">Time</label>
                  <input type="time" class="form-control" id="eventTime" name="eventTime" required>
                </div>
              </div>
              <div class="mb-3">
                <label for="eventLocation" class="form-label">Location</label>
                <input type="text" class="form-control" id="eventLocation" name="eventLocation"
                       placeholder="e.g., Main Hall" required>
              </div>
              <div class="mb-3">
                <label for="eventDesc" class="form-label">Description (Optional)</label>
                <textarea class="form-control" id="eventDesc" name="eventDesc" rows="3"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="submit" class="btn btn-primary">Save Schedule</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <%-- Modal ubah --%>
    <div class="modal fade" id="editScheduleModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Schedule</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="editScheduleForm" action="${ctx}/schedule/update" method="post">
            <t:csrf/>
            <input type="hidden" name="id">
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Event Name</label>
                <input type="text" class="form-control" name="eventName" required>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Start Date</label>
                  <input type="date" class="form-control" name="eventDate" required>
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Time</label>
                  <input type="time" class="form-control" name="eventTime" required>
                </div>
              </div>
              <div class="mb-3">
                <label class="form-label">Location</label>
                <input type="text" class="form-control" name="eventLocation" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Description (Optional)</label>
                <textarea class="form-control" name="eventDesc" rows="3"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="submit" class="btn btn-primary">Update Schedule</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </jsp:body>
</t:layout>
