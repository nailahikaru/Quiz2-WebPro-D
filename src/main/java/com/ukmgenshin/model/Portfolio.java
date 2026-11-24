package com.ukmgenshin.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    private long id;
    private Long userId;
    private String authorName;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private List<PortfolioImage> images;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setImages(List<PortfolioImage> images) {
        this.images = images;
    }


    public List<PortfolioImage> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

}
