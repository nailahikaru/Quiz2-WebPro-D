<%@ page contentType="text/html;charset=UTF-8" %>
<t:layout title="Cash Flow" theme="app">
  <jsp:attribute name="scripts">
    <script>
      // Isi modal "Edit Transaction" dengan data baris yang diklik
      document.querySelectorAll('.btn-edit-cash').forEach(function (btn) {
        btn.addEventListener('click', function () {
          var f = document.getElementById('editCashForm');
          f.querySelector('[name=id]').value = btn.dataset.id;
          f.querySelector('[name=type]').value = btn.dataset.type;
          f.querySelector('[name=amount]').value = btn.dataset.amount;
          f.querySelector('[name=description]').value = btn.dataset.description;
          f.querySelector('[name=date]').value = btn.dataset.date;
          new bootstrap.Modal(document.getElementById('editCashModal')).show();
        });
      });
    </script>
  </jsp:attribute>
  <jsp:body>
    <style>
      body { background-color: #f8f9fa; }
      .card-income { background: linear-gradient(135deg, #4DD599, #c9fae3); color: white; }
      .card-expense { background: linear-gradient(135deg, #FF6B6B, #ffd8d8); color: white; }
      .card-balance { background: linear-gradient(135deg, #8ECFFF, #dcf0ff); color: white; }
      .card-summary h6 { font-weight: 500; opacity: 0.9; }
      .card-summary h3 { font-weight: 700; margin-bottom: 0; }
    </style>

    <div class="container pb-5 my-4">

      <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 class="mb-0 fw-bold">Cash Flow</h2>
          <p class="text-muted mb-0">Manage UKM's income and expenses</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCashModal">
          <i class="bi bi-plus-lg"></i> Add Transaction
        </button>
      </div>

      <t:alerts/>

      <div class="row mb-4">
        <div class="col-md-4 mb-3">
          <div class="card card-summary card-income shadow-sm border-0 h-100">
            <div class="card-body">
              <h6>Total Income</h6>
              <h3>Rp ${u:rupiah(totalIncome)}</h3>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="card card-summary card-expense shadow-sm border-0 h-100">
            <div class="card-body">
              <h6>Total Expense</h6>
              <h3>Rp ${u:rupiah(totalExpense)}</h3>
            </div>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="card card-summary card-balance shadow-sm border-0 h-100">
            <div class="card-body">
              <h6>Current Balance</h6>
              <h3>Rp ${u:rupiah(balance)}</h3>
            </div>
          </div>
        </div>
      </div>

      <div class="card border-0 shadow-sm">
        <div class="card-body p-4">
          <div class="table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>No</th>
                  <th>Date</th>
                  <th>Description</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Added By</th>
                  <th class="text-center">Action</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="c" items="${cashes}" varStatus="st">
                  <tr>
                    <td>${st.count}</td>
                    <td>${u:date(c.txDate, 'dd MMM yyyy')}</td>
                    <td><strong><c:out value="${c.description}"/></strong></td>
                    <td>
                      <c:choose>
                        <c:when test="${c.income}"><span class="badge bg-success">Income</span></c:when>
                        <c:otherwise><span class="badge bg-danger">Expense</span></c:otherwise>
                      </c:choose>
                    </td>
                    <td class="${c.income ? 'text-success' : 'text-danger'} fw-bold">
                      ${c.income ? '+' : '-'} Rp ${u:rupiah(c.amount)}
                    </td>
                    <td><small class="text-muted"><c:out value="${empty c.userName ? 'Unknown' : c.userName}"/></small></td>
                    <td class="text-center">
                      <button type="button" class="btn btn-sm btn-outline-warning btn-edit-cash" title="Edit"
                              data-id="${c.id}"
                              data-type="${c.type}"
                              data-amount="${c.amount}"
                              data-description="<c:out value='${c.description}'/>"
                              data-date="${u:iso(c.txDate)}">
                        <i class="bi bi-pencil"></i>
                      </button>
                      <form action="${ctx}/cash/delete" method="post" class="d-inline"
                            onsubmit="return confirm('Delete this transaction?');">
                        <t:csrf/>
                        <input type="hidden" name="id" value="${c.id}">
                        <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete">
                          <i class="bi bi-trash"></i>
                        </button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty cashes}">
                  <tr>
                    <td colspan="7" class="text-center text-muted py-5">
                      <i class="bi bi-wallet2 fs-1 d-block mb-3"></i>
                      No transactions found. Click "Add Transaction" to start.
                    </td>
                  </tr>
                </c:if>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <%-- Modal tambah --%>
    <div class="modal fade" id="addCashModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add New Transaction</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form action="${ctx}/cash/store" method="post">
            <t:csrf/>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Transaction Type</label>
                <select class="form-select" name="type" required>
                  <option value="" disabled selected>-- Choose Type --</option>
                  <option value="income">Income (Uang Masuk)</option>
                  <option value="expense">Expense (Uang Keluar)</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Amount (Rp)</label>
                <input type="number" class="form-control" name="amount" placeholder="e.g., 50000" required min="1">
              </div>
              <div class="mb-3">
                <label class="form-label">Description</label>
                <input type="text" class="form-control" name="description" placeholder="e.g., Konsumsi Rapat" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Date</label>
                <input type="date" class="form-control" name="date" required value="${today}">
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="submit" class="btn btn-primary">Save Transaction</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <%-- Modal ubah --%>
    <div class="modal fade" id="editCashModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Transaction</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="editCashForm" action="${ctx}/cash/update" method="post">
            <t:csrf/>
            <input type="hidden" name="id">
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">Transaction Type</label>
                <select class="form-select" name="type" required>
                  <option value="income">Income (Uang Masuk)</option>
                  <option value="expense">Expense (Uang Keluar)</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Amount (Rp)</label>
                <input type="number" class="form-control" name="amount" required min="1">
              </div>
              <div class="mb-3">
                <label class="form-label">Description</label>
                <input type="text" class="form-control" name="description" required>
              </div>
              <div class="mb-3">
                <label class="form-label">Date</label>
                <input type="date" class="form-control" name="date" required>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="submit" class="btn btn-primary">Update Transaction</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </jsp:body>
</t:layout>
