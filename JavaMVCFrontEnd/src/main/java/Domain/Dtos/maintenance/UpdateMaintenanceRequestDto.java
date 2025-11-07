package Domain.Dtos.maintenance;

public class UpdateMaintenanceRequestDto {
    private Long id;
    private String description;
    private String type;
    private String maintenanceDate;

    public UpdateMaintenanceRequestDto() {}

    public UpdateMaintenanceRequestDto(Long id, String description, String type, String maintenanceDate) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.maintenanceDate = maintenanceDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(String maintenanceDate) { this.maintenanceDate = maintenanceDate; }
}