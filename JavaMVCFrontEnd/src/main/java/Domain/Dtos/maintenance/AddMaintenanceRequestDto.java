package Domain.Dtos.maintenance;

public class AddMaintenanceRequestDto {
    private String description;
    private String type;
    private String maintenanceDate;
    private Long carId;

    public AddMaintenanceRequestDto() {}

    public AddMaintenanceRequestDto(String description, String type, String maintenanceDate, Long carId) {
        this.description = description;
        this.type = type;
        this.maintenanceDate = maintenanceDate;
        this.carId = carId;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(String maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
}