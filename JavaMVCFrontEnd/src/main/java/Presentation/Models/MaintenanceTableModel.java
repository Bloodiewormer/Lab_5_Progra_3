package Presentation.Models;

import Domain.Dtos.maintenance.MaintenanceResponseDto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Description", "Type", "Date", "Car ID"};
    private final List<MaintenanceResponseDto> maintenances = new ArrayList<>();

    @Override
    public int getRowCount() {
        return maintenances.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MaintenanceResponseDto maintenance = maintenances.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> maintenance.getId();
            case 1 -> maintenance.getDescription();
            case 2 -> maintenance.getType();
            case 3 -> maintenance.getMaintenanceDate();
            case 4 -> maintenance.getCarId();
            default -> null;
        };
    }

    public void setMaintenances(List<MaintenanceResponseDto> newMaintenances) {
        this.maintenances.clear();
        if (newMaintenances != null) {
            this.maintenances.addAll(newMaintenances);
        }
        fireTableDataChanged();
    }

    public void addMaintenance(MaintenanceResponseDto maintenance) {
        this.maintenances.add(maintenance);
        fireTableRowsInserted(maintenances.size() - 1, maintenances.size() - 1);
    }

    public void updateMaintenance(int rowIndex, MaintenanceResponseDto maintenance) {
        this.maintenances.set(rowIndex, maintenance);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void removeMaintenance(int rowIndex) {
        this.maintenances.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public MaintenanceResponseDto getMaintenanceAt(int rowIndex) {
        return maintenances.get(rowIndex);
    }
}