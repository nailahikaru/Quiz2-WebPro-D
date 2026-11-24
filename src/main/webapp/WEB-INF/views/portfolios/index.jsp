<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Portofolio Karya" theme="c">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h2 fw-semibold mb-0">Portofolio Karya</h1>
    <c:if test="${not empty sessionScope.authUser}">
      <a href="${ctx}/portfolios/create" class="btn btn-primary">
        <i class="bi bi-plus-lg"></i> Tambah
      </a>
    </c:if>
  </div>

  <t:alerts/>

  <div class="row g-4">
    <c:forEach var="p" items="${portfolios}">
      <div class="col-12 col-md-6 col-lg-4">
        <div class="card card-glass h-100 overflow-hidden">

          <div class="d-flex align-items-center gap-2 p-3">
            <div class="avatar-circle">${u:initial(p.authorName)}</div>
            <span class="fw-semibold"><c:out value="${empty p.authorName ? 'Admin' : p.authorName}"/></span>
          </div>

          <div id="carousel-${p.id}" class="carousel slide">
            <c:if test="${fn:length(p.images) > 1}">
              <div class="carousel-indicators">
                <c:forEach var="img" items="${p.images}" varStatus="st">
                  <button type="button"
                          data-bs-target="#carousel-${p.id}"
                          data-bs-slide-to="${st.index}"
                          class="${st.first ? 'active' : ''}"
                          aria-label="Gambar ${st.count}"></button>
                </c:forEach>
              </div>
            </c:if>

            <div class="carousel-inner">
              <c:forEach var="img" items="${p.images}" varStatus="st">
                <div class="carousel-item ${st.first ? 'active' : ''}">
                  <img src="${ctx}/uploads/<c:out value='${img.path}'/>" class="d-block w-100 portfolio-img"
                       alt="<c:out value='${p.title}'/>">
                </div>
              </c:forEach>
            </div>

            <c:if test="${fn:length(p.images) > 1}">
              <button class="carousel-control-prev" type="button"
                      data-bs-target="#carousel-${p.id}" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Sebelumnya</span>
              </button>
              <button class="carousel-control-next" type="button"
                      data-bs-target="#carousel-${p.id}" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Berikutnya</span>
              </button>
            </c:if>
          </div>

          <div class="card-body">
            <h2 class="h6 fw-semibold"><c:out value="${p.title}"/></h2>
            <c:if test="${not empty p.description}">
              <p class="small mb-2">${u:nl2br(p.description)}</p>
            </c:if>
            <small class="text-muted">${u:date(p.createdAt, 'dd MMM yyyy')}</small>

            <c:if test="${not empty sessionScope.authUser}">
              <div class="mt-3 d-flex gap-2">
                <a href="${ctx}/portfolios/edit?id=${p.id}" class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-pencil"></i> Ubah
                </a>
                <form action="${ctx}/portfolios/delete" method="post"
                      onsubmit="return confirm('Hapus portofolio ini beserta semua gambarnya?')">
                  <t:csrf/>
                  <input type="hidden" name="id" value="${p.id}">
                  <button type="submit" class="btn btn-sm btn-outline-danger">
                    <i class="bi bi-trash"></i> Hapus
                  </button>
                </form>
              </div>
            </c:if>
          </div>

        </div>
      </div>
    </c:forEach>
    <c:if test="${empty portfolios}">
      <p class="text-center">Belum ada portofolio.</p>
    </c:if>
  </div>
</t:layout>
