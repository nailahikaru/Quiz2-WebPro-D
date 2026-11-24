<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="loggedIn" value="${not empty sessionScope.authUser}"/>
<t:layout title="${event.name}" theme="c">
  <jsp:attribute name="scripts">
    <script>
      // Isi modal "Ubah pendaftar" dengan data baris yang diklik
      document.querySelectorAll('.btn-edit-reg').forEach(function (btn) {
        btn.addEventListener('click', function () {
          var f = document.getElementById('editRegForm');
          f.querySelector('[name=id]').value = btn.dataset.id;
          f.querySelector('[name=name]').value = btn.dataset.name;
          f.querySelector('[name=division]').value = btn.dataset.division;
          f.querySelector('[name=reason]').value = btn.dataset.reason;
          new bootstrap.Modal(document.getElementById('editRegModal')).show();
        });
      });
      // Ganti nama divisi lewat prompt
      document.querySelectorAll('.btn-rename-div').forEach(function (btn) {
        btn.addEventListener('click', function () {
          var name = prompt('Nama divisi baru:', btn.dataset.name);
          if (name !== null && name.trim() !== '') {
            var f = document.getElementById('renameDivForm');
            f.querySelector('[name=id]').value = btn.dataset.id;
            f.querySelector('[name=name]').value = name.trim();
            f.submit();
          }
        });
      });
    </script>
  </jsp:attribute>
  <jsp:body>
    <a href="${ctx}/events" class="btn btn-sm btn-outline-secondary mb-3">
      <i class="bi bi-arrow-left"></i> Kembali
    </a>

    <t:alerts/>

    <div class="row g-4">
      <div class="col-lg-6">
        <div class="card card-glass h-100">
          <div class="card-body">
            <h1 class="h3 fw-semibold"><c:out value="${event.name}"/></h1>
            <c:if test="${not empty event.description}">
              <p>${u:nl2br(event.description)}</p>
            </c:if>
            <c:if test="${not empty event.location}">
              <p class="mb-1"><i class="bi bi-geo-alt"></i> <c:out value="${event.location}"/></p>
            </c:if>
            <p class="mb-1">
              <i class="bi bi-calendar-event"></i>
              ${u:date(event.startDate, 'dd MMM yyyy')} – ${u:date(event.endDate, 'dd MMM yyyy')}
            </p>
            <p class="mb-1">
              <i class="bi bi-hourglass-split"></i>
              Batas pendaftaran: ${u:date(event.registrationDeadline, 'dd MMM yyyy')}
            </p>
            <p class="mb-2">
              <i class="bi bi-people"></i>
              Terisi ${event.seatsTaken} / ${event.quota} (sisa ${event.seatsLeft})
            </p>
            <p class="mb-1"><i class="bi bi-diagram-3"></i> Divisi yang dibuka:</p>
            <div class="d-flex flex-wrap gap-2">
              <c:forEach var="d" items="${event.divisions}">
                <span class="badge bg-primary d-inline-flex align-items-center gap-1">
                  <c:out value="${d.name}"/>
                  <c:if test="${loggedIn}">
                    <button type="button" class="btn btn-link p-0 text-white btn-rename-div"
                            data-id="${d.id}" data-name="<c:out value='${d.name}'/>" title="Ubah nama">
                      <i class="bi bi-pencil"></i>
                    </button>
                    <form action="${ctx}/events/division-delete" method="post" class="d-inline"
                          onsubmit="return confirm('Hapus divisi ini?')">
                      <t:csrf/>
                      <input type="hidden" name="id" value="${d.id}">
                      <button type="submit" class="btn btn-link p-0 text-white" title="Hapus">
                        <i class="bi bi-x-lg"></i>
                      </button>
                    </form>
                  </c:if>
                </span>
              </c:forEach>
            </div>

            <c:if test="${loggedIn}">
              <form action="${ctx}/events/division-add" method="post" class="d-flex gap-2 mt-3">
                <t:csrf/>
                <input type="hidden" name="event_id" value="${event.id}">
                <input type="text" name="name" maxlength="50" class="form-control form-control-sm"
                       placeholder="Tambah divisi baru" required>
                <button type="submit" class="btn btn-sm btn-outline-primary text-nowrap">
                  <i class="bi bi-plus-lg"></i> Tambah
                </button>
              </form>
              <form id="renameDivForm" action="${ctx}/events/division-rename" method="post" class="d-none">
                <t:csrf/>
                <input type="hidden" name="id">
                <input type="hidden" name="name">
              </form>
            </c:if>
          </div>
        </div>
      </div>

      <div class="col-lg-6">
        <div class="card card-glass h-100">
          <div class="card-body">
            <h2 class="h5 fw-semibold mb-3">Daftar jadi panitia</h2>

            <c:choose>
              <c:when test="${not event.registrationOpen}">
                <div class="alert alert-secondary mb-0">Pendaftaran sudah ditutup.</div>
              </c:when>
              <c:when test="${event.seatsLeft < 1}">
                <div class="alert alert-danger mb-0">Kuota sudah penuh.</div>
              </c:when>
              <c:when test="${not loggedIn}">
                <div class="alert alert-info mb-0">
                  Silakan <a href="${ctx}/login">login</a> atau
                  <a href="${ctx}/register">register</a> untuk mendaftar.
                </div>
              </c:when>
              <c:otherwise>
                <form action="${ctx}/registrations/store" method="post">
                  <t:csrf/>
                  <input type="hidden" name="event_id" value="${event.id}">

                  <div class="mb-3">
                    <label for="name" class="form-label">Nama lengkap</label>
                    <input type="text" id="name" name="name"
                           class="form-control <c:if test='${not empty errors.name}'>is-invalid</c:if>"
                           value="<c:out value='${old.name}'/>">
                  </div>

                  <div class="mb-3">
                    <label for="nrp" class="form-label">NRP (10 digit angka)</label>
                    <input type="text" id="nrp" name="nrp"
                           class="form-control <c:if test='${not empty errors.nrp}'>is-invalid</c:if>"
                           value="<c:out value='${old.nrp}'/>"
                           inputmode="numeric" maxlength="10" pattern="[0-9]{10}"
                           placeholder="Contoh: 5025251208">
                  </div>

                  <div class="mb-3">
                    <label for="division" class="form-label">Divisi</label>
                    <select id="division" name="division"
                            class="form-select <c:if test='${not empty errors.division}'>is-invalid</c:if>">
                      <option value="">-- Pilih divisi --</option>
                      <c:forEach var="d" items="${event.divisions}">
                        <option value="<c:out value='${d.name}'/>" ${old.division eq d.name ? 'selected' : ''}>
                          <c:out value="${d.name}"/>
                        </option>
                      </c:forEach>
                    </select>
                  </div>

                  <div class="mb-3">
                    <label for="reason" class="form-label">Alasan bergabung</label>
                    <textarea id="reason" name="reason" rows="3"
                              class="form-control <c:if test='${not empty errors.reason}'>is-invalid</c:if>"><c:out value="${old.reason}"/></textarea>
                  </div>

                  <button type="submit" class="btn btn-primary">Kirim pendaftaran</button>
                </form>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>

    <div class="card card-glass mt-4">
      <div class="card-body">
        <h2 class="h5 fw-semibold mb-3">Daftar pendaftar (${fn:length(registrations)})</h2>

        <div class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>Nama</th>
                <th>NRP</th>
                <th>Divisi</th>
                <th>Alasan</th>
                <th>Status</th>
                <c:if test="${loggedIn}"><th>Aksi</th></c:if>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="r" items="${registrations}">
                <tr>
                  <td><c:out value="${r.name}"/></td>
                  <td><c:out value="${r.nrp}"/></td>
                  <td><c:out value="${r.division}"/></td>
                  <td><c:out value="${r.reason}"/></td>
                  <td>
                    <c:choose>
                      <c:when test="${r.status eq 'diterima'}"><span class="badge bg-success">Diterima</span></c:when>
                      <c:when test="${r.status eq 'ditolak'}"><span class="badge bg-danger">Ditolak</span></c:when>
                      <c:otherwise><span class="badge bg-warning text-dark">Menunggu</span></c:otherwise>
                    </c:choose>
                  </td>
                  <c:if test="${loggedIn}">
                    <td>
                      <div class="d-flex gap-1">
                        <form action="${ctx}/registrations/status" method="post">
                          <t:csrf/>
                          <input type="hidden" name="id" value="${r.id}">
                          <input type="hidden" name="status" value="diterima">
                          <button type="submit" class="btn btn-sm btn-success">Terima</button>
                        </form>
                        <form action="${ctx}/registrations/status" method="post">
                          <t:csrf/>
                          <input type="hidden" name="id" value="${r.id}">
                          <input type="hidden" name="status" value="ditolak">
                          <button type="submit" class="btn btn-sm btn-danger">Tolak</button>
                        </form>
                        <button type="button" class="btn btn-sm btn-outline-primary btn-edit-reg" title="Ubah"
                                data-id="${r.id}"
                                data-name="<c:out value='${r.name}'/>"
                                data-division="<c:out value='${r.division}'/>"
                                data-reason="<c:out value='${r.reason}'/>">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <form action="${ctx}/registrations/delete" method="post"
                              onsubmit="return confirm('Hapus pendaftar ini?')">
                          <t:csrf/>
                          <input type="hidden" name="id" value="${r.id}">
                          <button type="submit" class="btn btn-sm btn-outline-danger" title="Hapus">
                            <i class="bi bi-trash"></i>
                          </button>
                        </form>
                      </div>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
              <c:if test="${empty registrations}">
                <tr>
                  <td colspan="${loggedIn ? 6 : 5}" class="text-center">Belum ada pendaftar.</td>
                </tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <c:if test="${loggedIn}">
      <div class="modal fade" id="editRegModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Ubah data pendaftar</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="editRegForm" action="${ctx}/registrations/update" method="post">
              <t:csrf/>
              <input type="hidden" name="id">
              <div class="modal-body">
                <div class="mb-3">
                  <label class="form-label">Nama lengkap</label>
                  <input type="text" class="form-control" name="name" maxlength="100" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Divisi</label>
                  <select class="form-select" name="division" required>
                    <c:forEach var="d" items="${event.divisions}">
                      <option value="<c:out value='${d.name}'/>"><c:out value="${d.name}"/></option>
                    </c:forEach>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Alasan bergabung</label>
                  <textarea class="form-control" name="reason" rows="3" required></textarea>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                <button type="submit" class="btn btn-primary">Simpan</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </c:if>
  </jsp:body>
</t:layout>
