package Presentation.Models;

import Domain.Dtos.cars.CarResponseDto;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CarsTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Make", "Model", "Year", "Owner"};
    private final List<CarResponseDto> cars = new ArrayList<>();

    @Override
    public int getRowCount() {
        return cars.size();
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
        CarResponseDto car = cars.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> car.getId();
            case 1 -> car.getMake();
            case 2 -> car.getModel();
            case 3 -> car.getYear();
            case 4 -> car.getOwner() != null ? car.getOwner().getUsername() : "Unknown";
            default -> null;
        };
    }

    public void setCars(List<CarResponseDto> newCars) {
        this.cars.clear();
        if (newCars != null) {
            this.cars.addAll(newCars);
        }
        fireTableDataChanged();
    }

    public void addCar(CarResponseDto car) {
        this.cars.add(car);
        fireTableRowsInserted(cars.size() - 1, cars.size() - 1);
    }

    public void updateCar(int rowIndex, CarResponseDto car) {
        this.cars.set(rowIndex, car);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void removeCar(int rowIndex) {
        this.cars.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public CarResponseDto getCarAt(int rowIndex) {
        return cars.get(rowIndex);
    }
}