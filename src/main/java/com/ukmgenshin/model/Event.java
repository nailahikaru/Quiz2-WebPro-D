package com.ukmgenshin.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Event {

    private long id;
    private Long userId;
    private String name;
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private int quota;
    private int seatsTaken;
    private LocalDateTime createdAt;
    private List<EventDivision> divisions;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getSeatsTaken() {
        return seatsTaken;
    }

    public void setSeatsTaken(int seatsTaken) {
        this.seatsTaken = seatsTaken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setDivisions(List<EventDivision> divisions) {
        this.divisions = divisions;
    }


    public List<EventDivision> getDivisions() {
        if (divisions == null) {
            divisions = new ArrayList<>();
        }
        return divisions;
    }

    /** Sisa kursi = kuota - pendaftar yang tidak ditolak. */
    public int getSeatsLeft() {
        return Math.max(0, quota - seatsTaken);
    }

    /** Pendaftaran masih dibuka sampai (dan termasuk) hari batas pendaftaran. */
    public boolean isRegistrationOpen() {
        return registrationDeadline != null && !LocalDate.now().isAfter(registrationDeadline);
    }

}
