<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Pengumuman" theme="c">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2 fw-semibold mb-0">Pengumuman</h1>
    <c:if test="${not empty sessionScope.authUser}">
      <a href="${ctx}/announcements/create" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Tambah
      </a>
    </c:if>
  </div>

  <t:alerts/>

  <c:forEach var="a" items="${announcements}">
    <div class="card card-glass mb-3">
      <div class="card-body">
        <div class="d-flex justify-content-between flex-wrap gap-2">
          <h2 class="h5 fw-semibold"><c:out value="${a.title}"/></h2>
          <small class="text-muted">${u:date(a.createdAt, 'dd MMM yyyy, HH:mm')}</small>
        </div>
        <p class="mb-2">${u:nl2br(a.content)}</p>
        <small class="text-muted">Oleh <c:out value="${empty a.authorName ? 'Admin' : a.authorName}"/></small>

        <c:if test="${not empty sessionScope.authUser}">
          <div class="mt-3 d-flex gap-2">
            <a href="${ctx}/announcements/edit?id=${a.id}" class="btn btn-sm btn-outline-primary">
              <i class="bi bi-pencil"></i> Ubah
            </a>
            <form action="${ctx}/announcements/delete" method="post"
                  onsubmit="return confirm('Hapus pengumuman ini?')">
              <t:csrf/>
              <input type="hidden" name="id" value="${a.id}">
              <button type="submit" class="btn btn-sm btn-outline-danger">
                <i class="bi bi-trash"></i> Hapus
              </button>
            </form>
          </div>
        </c:if>
      </div>
    </div>
  </c:forEach>
  <c:if test="${empty announcements}">
    <p class="text-center">Belum ada pengumuman.</p>
  </c:if>
</t:layout>
