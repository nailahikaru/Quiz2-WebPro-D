<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="isEdit" value="${not empty member}"/>
<t:layout title="${isEdit ? 'Ubah Anggota' : 'Tambah Anggota'}" theme="c">
  <h1 class="h2 fw-semibold mb-4">${isEdit ? 'Ubah Anggota' : 'Tambah Anggota'}</h1>
  <div class="card card-glass">
    <div class="card-body">
      <form action="${ctx}/users/${isEdit ? 'update' : 'store'}" method="post">
        <t:csrf/>
        <c:if test="${isEdit}"><input type="hidden" name="id" value="${member.id}"></c:if>

        <div class="mb-3">
          <label for="name" class="form-label">Nama</label>
          <input type="text" id="name" name="name"
                 class="form-control <c:if test='${not empty errors.name}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? member.name : old.name}'/>" required>
          <c:if test="${not empty errors.name}"><div class="invalid-feedback"><c:out value="${errors.name}"/></div></c:if>
        </div>

        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input type="email" id="email" name="email"
                 class="form-control <c:if test='${not empty errors.email}'>is-invalid</c:if>"
                 value="<c:out value='${empty old ? member.email : old.email}'/>" required>
          <c:if test="${not empty errors.email}"><div class="invalid-feedback"><c:out value="${errors.email}"/></div></c:if>
        </div>

        <div class="mb-3">
          <label for="password" class="form-label">
            Password <c:if test="${isEdit}"><small class="text-muted">(kosongkan jika tidak diganti)</small></c:if>
          </label>
          <input type="password" id="password" name="password"
                 class="form-control <c:if test='${not empty errors.password}'>is-invalid</c:if>"
                 ${isEdit ? '' : 'required'}>
          <c:if test="${not empty errors.password}"><div class="invalid-feedback"><c:out value="${errors.password}"/></div></c:if>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">Simpan</button>
          <a href="${ctx}/users" class="btn btn-outline-secondary">Batal</a>
        </div>
      </form>
    </div>
  </div>
</t:layout>
