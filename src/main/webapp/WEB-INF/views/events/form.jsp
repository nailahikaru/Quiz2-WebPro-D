<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="isEdit" value="${not empty event}"/>
<t:layout title="${isEdit ? 'Ubah Event' : 'Tambah Event'}" theme="c">
  <h1 class="h2 fw-semibold mb-4">${isEdit ? 'Ubah Event' : 'Tambah Event'}</h1>
  <div class="card card-glass">
    <div class="card-body">
      <form action="${ctx}/events/${isEdit ? 'update' : 'store'}" method="post">
        <t:csrf/>
        <c:if test="${isEdit}"><input type="hidden" name="id" value="${event.id}"></c:if>

        <div class="mb-3">
          <label for="name" class="form-label">Nama event</label>
          <input type="text" id="name" name="name"
                 class="form-control <c:if test='${not empty errors.name}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? event.name : old.name}'/>">
          <c:if test="${not empty errors.name}"><div class="invalid-feedback"><c:out value="${errors.name}"/></div></c:if>
        </div>

        <div class="mb-3">
          <label for="description" class="form-label">Deskripsi (opsional)</label>
          <textarea id="description" name="description" rows="4"
                    class="form-control"><c:out value="${empty old ? event.description : old.description}"/></textarea>
        </div>

        <div class="mb-3">
          <label for="location" class="form-label">Lokasi (opsional)</label>
          <input type="text" id="location" name="location"
                 class="form-control <c:if test='${not empty errors.location}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? event.location : old.location}'/>">
          <c:if test="${not empty errors.location}"><div class="invalid-feedback"><c:out value="${errors.location}"/></div></c:if>
        </div>

        <div class="row">
          <div class="col-md-4 mb-3">
            <label for="start_date" class="form-label">Tanggal mulai</label>
            <input type="date" id="start_date" name="start_date"
                   class="form-control <c:if test='${not empty errors.start_date}'>is-invalid</c:if>"
                   value="<c:out value='${empty old ? u:iso(event.startDate) : old.start_date}'/>">
            <c:if test="${not empty errors.start_date}"><div class="invalid-feedback"><c:out value="${errors.start_date}"/></div></c:if>
          </div>
          <div class="col-md-4 mb-3">
            <label for="end_date" class="form-label">Tanggal selesai</label>
            <input type="date" id="end_date" name="end_date"
                   class="form-control <c:if test='${not empty errors.end_date}'>is-invalid</c:if>"
                   value="<c:out value='${empty old ? u:iso(event.endDate) : old.end_date}'/>">
            <c:if test="${not empty errors.end_date}"><div class="invalid-feedback"><c:out value="${errors.end_date}"/></div></c:if>
          </div>
          <div class="col-md-4 mb-3">
            <label for="registration_deadline" class="form-label">Batas pendaftaran</label>
            <input type="date" id="registration_deadline" name="registration_deadline"
                   class="form-control <c:if test='${not empty errors.registration_deadline}'>is-invalid</c:if>"
                   value="<c:out value='${empty old ? u:iso(event.registrationDeadline) : old.registration_deadline}'/>">
            <c:if test="${not empty errors.registration_deadline}"><div class="invalid-feedback"><c:out value="${errors.registration_deadline}"/></div></c:if>
          </div>
        </div>

        <div class="mb-3">
          <label for="quota" class="form-label">Kuota panitia</label>
          <input type="number" id="quota" name="quota" min="1"
                 class="form-control <c:if test='${not empty errors.quota}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? event.quota : old.quota}'/>">
          <c:if test="${not empty errors.quota}"><div class="invalid-feedback"><c:out value="${errors.quota}"/></div></c:if>
        </div>

        <div class="mb-3">
          <label for="divisions" class="form-label">Divisi yang dibuka</label>
          <textarea id="divisions" name="divisions" rows="6"
                    class="form-control <c:if test='${not empty errors.divisions}'>is-invalid</c:if>"><c:out value="${divisionText}"/></textarea>
          <div class="form-text">Tulis satu divisi per baris. Hanya divisi ini yang muncul di formulir pendaftaran event.</div>
          <c:if test="${not empty errors.divisions}"><div class="invalid-feedback"><c:out value="${errors.divisions}"/></div></c:if>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">Simpan</button>
          <a href="${ctx}/events" class="btn btn-outline-secondary">Batal</a>
        </div>
      </form>
    </div>
  </div>
</t:layout>
