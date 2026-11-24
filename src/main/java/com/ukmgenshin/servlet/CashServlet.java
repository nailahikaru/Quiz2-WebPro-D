package com.ukmgenshin.servlet;

import com.ukmgenshin.dao.CashDao;
import com.ukmgenshin.model.Cash;
import com.ukmgenshin.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/** CRUD Kas: pemasukan/pengeluaran dan saldo (CashController). Semua aksi wajib login. */
@WebServlet("/cash/*")
public class CashServlet extends BaseServlet {

    private static final String NAV = "cash";
    private final CashDao dao = new CashDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!action(req).isEmpty()) {
            notFound(resp);
            return;
        }
        showList(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long income = dao.total("income");
        long expense = dao.total("expense");
        req.setAttribute("cashes", dao.findAll());
        req.setAttribute("totalIncome", income);
        req.setAttribute("totalExpense", expense);
        req.setAttribute("balance", income - expense);
        req.setAttribute("today", LocalDate.now().toString());
        view(req, resp, "cash/index", NAV);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (action(req)) {
            case "store":
                save(req, resp, null);
                break;
            case "update": {
                Cash c = dao.find(longParam(req, "id"));
                if (c == null) {
                    notFound(resp);
                    return;
                }
                save(req, resp, c);
                break;
            }
            case "delete": {
                Cash c = dao.find(longParam(req, "id"));
                if (c == null) {
                    notFound(resp);
                    return;
                }
                dao.delete(c.getId());
                flash(req, "Transaction deleted!");
                redirect(req, resp, "/cash");
                break;
            }
            default:
                notFound(resp);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, Cash existing)
            throws ServletException, IOException {
        String type = param(req, "type");
        String description = param(req, "description");
        LocalDate date = dateParam(req, "date");

        Validator v = new Validator()
                .required("description", description, "Deskripsi").max("description", description, 255, "Deskripsi");
        if (!type.equals("income") && !type.equals("expense")) {
            v.add("type", "Jenis transaksi harus income atau expense.");
        }
        long amount = 0;
        try {
            amount = Long.parseLong(param(req, "amount"));
        } catch (NumberFormatException ignored) {
            // ditangani di bawah
        }
        if (amount < 1) {
            v.add("amount", "Nominal harus berupa angka bulat minimal 1.");
        }
        if (date == null) {
            v.add("date", "Tanggal wajib diisi dengan format yang valid.");
        }

        if (v.fails()) {
            req.setAttribute("errors", v.errors());
            showList(req, resp);
            return;
        }

        Cash c = existing == null ? new Cash() : existing;
        c.setType(type);
        c.setAmount(amount);
        c.setDescription(description);
        c.setTxDate(date);
        if (existing == null) {
            c.setUserId(currentUserId(req));
            dao.create(c);
            flash(req, "Transaction added successfully!");
        } else {
            dao.update(c);
            flash(req, "Transaction updated successfully!");
        }
        redirect(req, resp, "/cash");
    }
}
