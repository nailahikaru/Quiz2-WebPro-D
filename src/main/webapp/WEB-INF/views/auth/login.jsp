<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Login" theme="home">
  <section>
    <div class="container" style="max-width: 28rem; padding-top: 3rem;">
      <div class="card shadow-sm" style="background-color: rgba(255,255,255,0.7); backdrop-filter: blur(10px);">
        <div class="card-body p-4">
          <h2 class="fw-semibold text-center mb-4">Login</h2>

          <form method="post" action="${ctx}/login"
                onsubmit="this.querySelector('button[type=submit]').disabled = true">
            <t:csrf/>

            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input type="email" id="email" name="email" value="<c:out value='${old.email}'/>"
                     class="form-control <c:if test='${not empty errors.email}'>is-invalid</c:if>" required autofocus>
              <c:if test="${not empty errors.email}">
                <div class="invalid-feedback"><c:out value="${errors.email}"/></div>
              </c:if>
            </div>

            <div class="mb-3">
              <label for="password" class="form-label">Password</label>
              <input type="password" id="password" name="password"
                     class="form-control <c:if test='${not empty errors.password}'>is-invalid</c:if>" required>
              <c:if test="${not empty errors.password}">
                <div class="invalid-feedback"><c:out value="${errors.password}"/></div>
              </c:if>
            </div>

            <div class="form-check mb-3">
              <input type="checkbox" class="form-check-input" id="remember" name="remember">
              <label class="form-check-label" for="remember">Ingat saya</label>
            </div>

            <button type="submit" class="btn btn-primary w-100">Login</button>
          </form>

          <p class="text-center mt-3 mb-0">
            Belum punya akun? <a href="${ctx}/register">Daftar</a>
          </p>
        </div>
      </div>
    </div>
  </section>
</t:layout>
