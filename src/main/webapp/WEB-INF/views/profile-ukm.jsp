<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="About us" theme="about">
  <section class="jumbotron text-center">
    <div class="container">
      <h1 class="display-4">Welcome to our beloved UKM</h1>
      <p class="lead fw-medium mb-0">we are delighted to meet you</p>
    </div>
  </section>

  <section id="profile-about">
    <div class="container">
      <div class="d-flex flex-column flex-lg-row align-items-center justify-content-center gap-4 gap-lg-5">
        <div class="profile-card">
          <img src="${ctx}/image/paimon-bingung.svg" alt="Paimon confused" loading="lazy">
        </div>

        <div class="profile-content">
          <h2 class="nama">What is UKM Genshin?</h2>
          <div class="info">
            <p>
              UKM or Unit Kegiatan Mahasiswa, is a student activity unit established specifically for students of Akademiya Institute. Designed as a creative hub, this UKM brings together students who share a passion for gaming and the arts, offering a space where creativity, collaboration, and self-expression can thrive.
            </p>
          </div>
        </div>
      </div>
    </div>
  </section>

  <section class="jumbotron-team text-center">
    <div class="container">
      <h1 class="display-4">Meet our team!</h1>
      <p class="lead fw-medium mb-0">get to know us!</p>
    </div>
  </section>

  <section id="profile">
    <div class="container">
      <div class="d-flex flex-column flex-lg-row align-items-center justify-content-center gap-4 gap-lg-5">
        <div class="profile-card">
          <img src="${ctx}/image/Rafifah.png" alt="Rafifah photo" loading="lazy">
        </div>
        <div class="profile-content">
          <h2 class="nama">Rafifah Rahmah Admayana</h2>
          <div class="info">
            <h4 class="poppins-regular">Engineer 1</h4>
            <p>She handle our Announcements, portfolio gallery, and event registration information!</p>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="d-flex flex-column flex-lg-row align-items-center justify-content-center gap-4 gap-lg-5">
        <div class="profile-card">
          <img src="${ctx}/image/Naila.png" alt="Naila photo" loading="lazy">
        </div>
        <div class="profile-content">
          <h2 class="nama">Naila Hikaru</h2>
          <div class="info">
            <h4 class="poppins-regular">Engineer 2</h4>
            <p>She handle our Login, Register, Logout, UKM profile, and Member profiles!</p>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <div class="d-flex flex-column flex-lg-row align-items-center justify-content-center gap-4 gap-lg-5">
        <div class="profile-card">
          <img src="${ctx}/image/Hasna.png" alt="Hasna photo" loading="lazy">
        </div>
        <div class="profile-content">
          <h2 class="nama">Hasna Nabila Hanim</h2>
          <div class="info">
            <h4 class="poppins-regular">Engineer 3</h4>
            <p>She handle our Schedule, Cash Flow, and Activity attendance!</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</t:layout>
