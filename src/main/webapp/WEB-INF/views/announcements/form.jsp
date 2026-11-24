<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="isEdit" value="${not empty announcement}"/>
<t:layout title="${isEdit ? 'Ubah Pengumuman' : 'Tambah Pengumuman'}" theme="c">
  <h1 class="h2 fw-semibold mb-4">${isEdit ? 'Ubah Pengumuman' : 'Tambah Pengumuman'}</h1>
  <div class="card card-glass">
    <div class="card-body">
      <form action="${ctx}/announcements/${isEdit ? 'update' : 'store'}" method="post">
        <t:csrf/>
        <c:if test="${isEdit}"><input type="hidden" name="id" value="${announcement.id}"></c:if>

        <div class="mb-3">
          <label for="title" class="form-label">Judul</label>
          <input type="text" id="title" name="title"
                 class="form-control <c:if test='${not empty errors.title}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? announcement.title : old.title}'/>">
          <c:if test="${not empty errors.title}">
            <div class="invalid-feedback"><c:out value="${errors.title}"/></div>
          </c:if>
        </div>

        <div class="mb-3">
          <label for="content" class="form-label">Isi pengumuman</label>
          <textarea id="content" name="content" rows="6"
                    class="form-control <c:if test='${not empty errors.content}'>is-invalid</c:if>"><c:out value="${empty old ? announcement.content : old.content}"/></textarea>
          <c:if test="${not empty errors.content}">
            <div class="invalid-feedback"><c:out value="${errors.content}"/></div>
          </c:if>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">Simpan</button>
          <a href="${ctx}/announcements" class="btn btn-outline-secondary">Batal</a>
        </div>
      </form>
    </div>
  </div>
</t:layout>
