<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:set var="msg" value="${requestScope['jakarta.servlet.error.message']}"/>
<t:layout title="Terjadi masalah" theme="c">
  <div class="card card-glass text-center mt-5">
    <div class="card-body p-5">
      <h1 class="display-4 fw-semibold"><c:out value="${empty code ? 'Error' : code}"/></h1>
      <p class="lead mb-4">
        <c:choose>
          <c:when test="${code == 404}">Halaman yang kamu cari tidak ditemukan.</c:when>
          <c:when test="${code == 403}"><c:out value="${empty msg ? 'Akses ditolak.' : msg}"/></c:when>
          <c:otherwise>Maaf, terjadi kesalahan di server. Coba lagi sebentar lagi.</c:otherwise>
        </c:choose>
      </p>
      <a href="${ctx}/" class="btn btn-primary">Kembali ke Home</a>
    </div>
  </div>
</t:layout>
