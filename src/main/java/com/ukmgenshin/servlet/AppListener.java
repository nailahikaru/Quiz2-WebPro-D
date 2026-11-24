package com.ukmgenshin.servlet;

import com.ukmgenshin.util.DBUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Menyediakan ${ctx} (context path) untuk semua JSP dan menutup connection pool saat undeploy. */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("ctx", sce.getServletContext().getContextPath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBUtil.shutdown();
    }
}
