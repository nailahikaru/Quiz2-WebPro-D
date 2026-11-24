<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Anggota" theme="c">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2 fw-semibold mb-0">Anggota UKM</h1>
    <a href="${ctx}/users/create" class="btn btn-primary">
      <i class="bi bi-plus-lg"></i> Tambah Anggota
    </a>
  </div>

  <t:alerts/>

  <div class="card card-glass">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table align-middle mb-0">
          <thead>
            <tr>
              <th>No</th>
              <th>Nama</th>
              <th>Email</th>
              <th>Bergabung</th>
              <th class="text-center">Aksi</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="m" items="${users}" varStatus="st">
              <tr>
                <td>${st.count}</td>
                <td>
                  <c:out value="${m.name}"/>
                  <c:if test="${m.id == sessionScope.authUser.id}"><span class="badge bg-info text-dark ms-1">Kamu</span></c:if>
                </td>
                <td><c:out value="${m.email}"/></td>
                <td>${u:date(m.createdAt, 'dd MMM yyyy')}</td>
                <td class="text-center">
                  <a href="${ctx}/users/edit?id=${m.id}" class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-pencil"></i> Ubah
                  </a>
                  <c:if test="${m.id != sessionScope.authUser.id}">
                    <form action="${ctx}/users/delete" method="post" class="d-inline"
                          onsubmit="return confirm('Hapus anggota ini? Tanda presensinya juga ikut terhapus.')">
                      <t:csrf/>
                      <input type="hidden" name="id" value="${m.id}">
                      <button type="submit" class="btn btn-sm btn-outline-danger">
                        <i class="bi bi-trash"></i> Hapus
                      </button>
                    </form>
                  </c:if>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty users}">
              <tr><td colspan="5" class="text-center">Belum ada anggota.</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</t:layout>
