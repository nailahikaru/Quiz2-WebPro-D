<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="isEdit" value="${not empty portfolio}"/>
<t:layout title="${isEdit ? 'Ubah Portofolio' : 'Tambah Portofolio'}" theme="c">
  <h1 class="h2 fw-semibold mb-4">${isEdit ? 'Ubah Portofolio' : 'Tambah Portofolio'}</h1>

  <t:alerts/>

  <div class="card card-glass">
    <div class="card-body">

      <%-- Gambar yang sudah ada (form hapus terpisah, tidak boleh bersarang di form utama) --%>
      <c:if test="${isEdit}">
        <p class="fw-semibold mb-2">Gambar saat ini</p>
        <div class="row g-3 mb-4">
          <c:forEach var="img" items="${portfolio.images}">
            <div class="col-6 col-md-3">
              <img src="${ctx}/uploads/<c:out value='${img.path}'/>" class="img-fluid rounded mb-2" alt="Gambar portofolio">
              <form action="${ctx}/portfolios/image-delete" method="post"
                    onsubmit="return confirm('Hapus gambar ini?')">
                <t:csrf/>
                <input type="hidden" name="image_id" value="${img.id}">
                <button type="submit" class="btn btn-sm btn-outline-danger w-100">
                  <i class="bi bi-trash"></i> Hapus
                </button>
              </form>
            </div>
          </c:forEach>
        </div>
      </c:if>

      <%-- token CSRF juga dikirim lewat query string supaya terbaca tanpa harus mem-parsing body multipart --%>
      <form action="${ctx}/portfolios/${isEdit ? 'update' : 'store'}?_csrf=${sessionScope.csrf}" method="post" enctype="multipart/form-data">
        <t:csrf/>
        <c:if test="${isEdit}"><input type="hidden" name="id" value="${portfolio.id}"></c:if>

        <div class="mb-3">
          <label for="title" class="form-label">Judul karya</label>
          <input type="text" id="title" name="title" class="form-control"
                 value="<c:out value='${empty old ? portfolio.title : old.title}'/>">
        </div>

        <div class="mb-3">
          <label for="description" class="form-label">Deskripsi (opsional)</label>
          <textarea id="description" name="description" rows="4"
                    class="form-control"><c:out value="${empty old ? portfolio.description : old.description}"/></textarea>
        </div>

        <div class="mb-3">
          <label for="images" class="form-label">${isEdit ? 'Tambah gambar (opsional)' : 'Gambar'}</label>
          <input type="file" id="images" name="images" class="form-control"
                 accept="image/png,image/jpeg,image/webp" multiple>
          <div class="form-text">Total 1 sampai 5 gambar per portofolio (JPG, PNG, atau WEBP), maksimal 10 MB per gambar.</div>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-primary">Simpan</button>
          <a href="${ctx}/portfolios" class="btn btn-outline-secondary">Batal</a>
        </div>
      </form>
    </div>
  </div>
</t:layout>
