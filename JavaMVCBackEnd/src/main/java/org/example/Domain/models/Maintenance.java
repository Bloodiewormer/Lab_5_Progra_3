package org.example.Domain.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType type;

    @Column(name = "maintenance_date", nullable = false)
    private LocalDateTime maintenanceDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false, foreignKey = @ForeignKey(name = "fk_car_maintenance"))
    private Car carMaintenance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Maintenance() {}

    public Maintenance(String description, MaintenanceType type, LocalDateTime maintenanceDate, Car carMaintenance) {
        this.description = description;
        this.type = type;
        this.maintenanceDate = maintenanceDate;
        this.carMaintenance = carMaintenance;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MaintenanceType getType() {
        return type;
    }

    public void setType(MaintenanceType type) {
        this.type = type;
    }

    public LocalDateTime getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDateTime maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public Car getCarMaintenance() {
        return carMaintenance;
    }

    public void setCarMaintenance(Car carMaintenance) {
        this.carMaintenance = carMaintenance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", maintenanceDate=" + maintenanceDate +
                '}';
    }
}