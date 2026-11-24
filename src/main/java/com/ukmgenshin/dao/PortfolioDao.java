package com.ukmgenshin.dao;

import com.ukmgenshin.model.Portfolio;
import com.ukmgenshin.model.PortfolioImage;
import com.ukmgenshin.util.Jdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PortfolioDao {

    private static final String SELECT =
            "SELECT p.*, u.name AS author_name FROM portfolios p "
            + "LEFT JOIN users u ON u.id = p.user_id ";

    private static Portfolio map(java.sql.ResultSet rs) throws SQLException {
        Portfolio p = new Portfolio();
        p.setId(rs.getLong("id"));
        p.setUserId(Jdbc.nullableLong(rs, "user_id"));
        p.setAuthorName(rs.getString("author_name"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setCreatedAt(Jdbc.dateTime(rs, "created_at"));
        p.setImages(new ArrayList<>());
        return p;
    }

    private static PortfolioImage mapImage(java.sql.ResultSet rs) throws SQLException {
        PortfolioImage i = new PortfolioImage();
        i.setId(rs.getLong("id"));
        i.setPortfolioId(rs.getLong("portfolio_id"));
        i.setPath(rs.getString("path"));
        return i;
    }

    public List<Portfolio> findAll() {
        List<Portfolio> list = Jdbc.list(SELECT + "ORDER BY p.created_at DESC, p.id DESC", PortfolioDao::map);
        Map<Long, Portfolio> byId = new LinkedHashMap<>();
        for (Portfolio p : list) {
            byId.put(p.getId(), p);
        }
        for (PortfolioImage img : Jdbc.list("SELECT * FROM portfolio_images ORDER BY id", PortfolioDao::mapImage)) {
            Portfolio owner = byId.get(img.getPortfolioId());
            if (owner != null) {
                owner.getImages().add(img);
            }
        }
        return list;
    }

    public Portfolio find(Long id) {
        if (id == null) {
            return null;
        }
        Portfolio p = Jdbc.one(SELECT + "WHERE p.id = ?", PortfolioDao::map, id);
        if (p != null) {
            p.setImages(images(p.getId()));
        }
        return p;
    }

    public List<PortfolioImage> images(long portfolioId) {
        return Jdbc.list("SELECT * FROM portfolio_images WHERE portfolio_id = ? ORDER BY id",
                PortfolioDao::mapImage, portfolioId);
    }

    public PortfolioImage findImage(Long imageId) {
        if (imageId == null) {
            return null;
        }
        return Jdbc.one("SELECT * FROM portfolio_images WHERE id = ?", PortfolioDao::mapImage, imageId);
    }

    /** Simpan portofolio beserta path gambarnya dalam satu transaksi. */
    public void create(Long userId, String title, String description, List<String> imagePaths) {
        Jdbc.tx(c -> {
            long id = Jdbc.insert(c, "INSERT INTO portfolios (user_id, title, description) VALUES (?, ?, ?)",
                    userId, title, description);
            for (String path : imagePaths) {
                Jdbc.insert(c, "INSERT INTO portfolio_images (portfolio_id, path) VALUES (?, ?)", id, path);
            }
        });
    }

    public void update(long id, String title, String description) {
        Jdbc.update("UPDATE portfolios SET title = ?, description = ? WHERE id = ?", title, description, id);
    }

    public void addImages(long portfolioId, List<String> imagePaths) {
        Jdbc.tx(c -> {
            for (String path : imagePaths) {
                Jdbc.insert(c, "INSERT INTO portfolio_images (portfolio_id, path) VALUES (?, ?)", portfolioId, path);
            }
        });
    }

    public void deleteImage(long imageId) {
        Jdbc.update("DELETE FROM portfolio_images WHERE id = ?", imageId);
    }

    public void delete(long id) {
        Jdbc.update("DELETE FROM portfolios WHERE id = ?", id);
    }
}
