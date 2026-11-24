<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Profile" theme="home">
  <section>
    <div class="container" style="max-width: 32rem; padding-top: 3rem;">
      <div class="card shadow-sm" style="background-color: rgba(255,255,255,0.7); backdrop-filter: blur(10px);">
        <div class="card-body p-4">
          <h2 class="fw-semibold text-center mb-4">Profil Saya</h2>

          <t:alerts showErrors="false"/>

          <form method="post" action="${ctx}/profile">
            <t:csrf/>

            <div class="mb-3">
              <label for="name" class="form-label">Nama</label>
              <input type="text" id="name" name="name"
                     value="<c:out value='${empty old ? sessionScope.authUser.name : old.name}'/>"
                     class="form-control <c:if test='${not empty errors.name}'>is-invalid</c:if>" required>
              <c:if test="${not empty errors.name}">
                <div class="invalid-feedback"><c:out value="${errors.name}"/></div>
              </c:if>
            </div>

            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input type="email" id="email" name="email"
                     value="<c:out value='${empty old ? sessionScope.authUser.email : old.email}'/>"
                     class="form-control <c:if test='${not empty errors.email}'>is-invalid</c:if>" required>
              <c:if test="${not empty errors.email}">
                <div class="invalid-feedback"><c:out value="${errors.email}"/></div>
              </c:if>
            </div>

            <div class="mb-3">
              <label for="password" class="form-label">Password baru <small class="text-muted">(kosongkan jika tidak diganti)</small></label>
              <input type="password" id="password" name="password"
                     class="form-control <c:if test='${not empty errors.password}'>is-invalid</c:if>">
              <c:if test="${not empty errors.password}">
                <div class="invalid-feedback"><c:out value="${errors.password}"/></div>
              </c:if>
            </div>

            <div class="mb-3">
              <label for="password_confirmation" class="form-label">Konfirmasi password baru</label>
              <input type="password" id="password_confirmation" name="password_confirmation" class="form-control">
            </div>

            <button type="submit" class="btn btn-primary w-100">Simpan perubahan</button>
          </form>
        </div>
      </div>
    </div>
  </section>
</t:layout>
