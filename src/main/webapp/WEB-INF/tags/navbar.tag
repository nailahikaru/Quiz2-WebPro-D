<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<nav class="navbar navbar-expand-lg navbar-light shadow-sm fixed-top">
  <div class="container">
    <a class="navbar-brand d-flex align-items-center gap-2" href="${ctx}/">
      <img src="${ctx}/image/logo.webp" alt="Logo UKM Genshin" width="40" height="40" class="align-text-top">
      <span>UKM Genshin <i class="bi bi-stars"></i></span>
    </a>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
            data-bs-target="#navbarNav" aria-controls="navbarNav"
            aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link ${nav eq 'home' ? 'active' : ''}" href="${ctx}/">Home</a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${nav eq 'about' ? 'active' : ''}" href="${ctx}/profile-ukm">About us</a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${nav eq 'announcements' ? 'active' : ''}" href="${ctx}/announcements">Pengumuman</a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${nav eq 'portfolios' ? 'active' : ''}" href="${ctx}/portfolios">Galeri</a>
        </li>
        <li class="nav-item">
          <a class="nav-link ${nav eq 'events' ? 'active' : ''}" href="${ctx}/events">Event</a>
        </li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <c:choose>
          <c:when test="${empty sessionScope.authUser}">
            <li class="nav-item">
              <a class="nav-link ${nav eq 'login' ? 'active' : ''}" href="${ctx}/login">Login</a>
            </li>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'register' ? 'active' : ''}" href="${ctx}/register">Register</a>
            </li>
          </c:when>
          <c:otherwise>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'schedule' ? 'active' : ''}" href="${ctx}/schedule">Jadwal</a>
            </li>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'presence' ? 'active' : ''}" href="${ctx}/presence">Presensi</a>
            </li>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'cash' ? 'active' : ''}" href="${ctx}/cash">Kas</a>
            </li>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'users' ? 'active' : ''}" href="${ctx}/users">Anggota</a>
            </li>
            <li class="nav-item">
              <a class="nav-link ${nav eq 'profile' ? 'active' : ''}" href="${ctx}/profile">Profile</a>
            </li>
            <li class="nav-item">
              <form method="post" action="${ctx}/logout">
                <t:csrf/>
                <button type="submit" class="nav-link btn btn-link">Logout</button>
              </form>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </div>
</nav>
