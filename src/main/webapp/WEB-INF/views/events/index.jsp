<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Event Kepanitiaan" theme="c">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2 fw-semibold mb-0">Event Kepanitiaan</h1>
    <c:if test="${not empty sessionScope.authUser}">
      <a href="${ctx}/events/create" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Tambah Event
      </a>
    </c:if>
  </div>

  <t:alerts/>

  <div class="row g-4">
    <c:forEach var="e" items="${events}">
      <div class="col-12 col-md-6 col-lg-4">
        <div class="card card-glass h-100">
          <div class="card-body d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start gap-2 mb-2">
              <h2 class="h5 fw-semibold mb-0"><c:out value="${e.name}"/></h2>
              <c:choose>
                <c:when test="${not e.registrationOpen}"><span class="badge bg-secondary">Ditutup</span></c:when>
                <c:when test="${e.seatsLeft < 1}"><span class="badge bg-danger">Penuh</span></c:when>
                <c:otherwise><span class="badge bg-success">Dibuka</span></c:otherwise>
              </c:choose>
            </div>

            <c:if test="${not empty e.location}">
              <p class="small mb-1"><i class="bi bi-geo-alt"></i> <c:out value="${e.location}"/></p>
            </c:if>
            <p class="small mb-1">
              <i class="bi bi-calendar-event"></i>
              ${u:date(e.startDate, 'dd MMM yyyy')} – ${u:date(e.endDate, 'dd MMM yyyy')}
            </p>
            <p class="small mb-1">
              <i class="bi bi-hourglass-split"></i>
              Batas daftar: ${u:date(e.registrationDeadline, 'dd MMM yyyy')}
            </p>
            <p class="small mb-3">
              <i class="bi bi-people"></i>
              Terisi ${e.seatsTaken} / ${e.quota}
            </p>

            <div class="mt-auto d-flex flex-wrap gap-2">
              <a href="${ctx}/events/show?id=${e.id}" class="btn btn-sm btn-primary">Detail dan Daftar</a>
              <c:if test="${not empty sessionScope.authUser}">
                <a href="${ctx}/events/edit?id=${e.id}" class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-pencil"></i> Ubah
                </a>
                <form action="${ctx}/events/delete" method="post"
                      onsubmit="return confirm('Hapus event ini beserta semua pendaftarnya?')">
                  <t:csrf/>
                  <input type="hidden" name="id" value="${e.id}">
                  <button type="submit" class="btn btn-sm btn-outline-danger">
                    <i class="bi bi-trash"></i> Hapus
                  </button>
                </form>
              </c:if>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
    <c:if test="${empty events}">
      <p class="text-center">Belum ada event.</p>
    </c:if>
  </div>
</t:layout>
