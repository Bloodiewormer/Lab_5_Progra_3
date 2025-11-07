package Domain.Dtos.maintenance;

public class MaintenanceResponseDto {
    private Long id;
    private String description;
    private String type;
    private String maintenanceDate;
    private Long carId;
    private String createdAt;
    private String updatedAt;

    public MaintenanceResponseDto() {}

    public MaintenanceResponseDto(Long id, String description, String type, String maintenanceDate, Long carId, String createdAt, String updatedAt) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.maintenanceDate = maintenanceDate;
        this.carId = carId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(String maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}