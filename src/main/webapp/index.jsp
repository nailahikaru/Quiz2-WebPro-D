<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="nav" value="home" scope="request"/>
<t:layout title="Home" theme="home">
  <section class="jumbotron text-center">
    <div class="container">
      <h1 class="nama display-4">Hello <c:out value="${empty sessionScope.authUser ? 'Traveler' : sessionScope.authUser.name}"/>!</h1>
      <p class="lead fw-medium mb-0">welcome to UKM Genshin</p>
    </div>
  </section>

  <section id="profile">
    <div class="container">
      <div class="d-flex flex-row flex-lg-column align-items-center justify-content-center gap-4 gap-lg-5">

        <div class="profile-card">
          <img src="${ctx}/image/Paimon.png" alt="Paimon photo" loading="lazy">
        </div>

        <h1 class="nama display-4">Hi Traveler!</h1>
        <div class="info">
          <h4 class="poppins-regular">Your journey awaits!</h4>
          <p>
            Teyvat's day and night both seem particularly short. The skies here are full of stars, but they aren't the same as the ones seen from my home. I wonder if the fates of people from Teyvat are also related to their constellations.
          </p>
        </div>

      </div>
    </div>
  </section>
</t:layout>
