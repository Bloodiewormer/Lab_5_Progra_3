package Presentation.Controllers;

import Domain.Dtos.auth.UserResponseDto;
import Domain.Dtos.cars.AddCarRequestDto;
import Domain.Dtos.cars.CarResponseDto;
import Domain.Dtos.cars.DeleteCarRequestDto;
import Domain.Dtos.cars.UpdateCarRequestDto;
import Presentation.Models.CarsTableModel;
import Presentation.Views.CarsView;
import Services.CarService;
import Utilities.EventType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Future;

public class CarsController extends Observable {
    private final CarsView view;
    private final CarService service;
    private final UserResponseDto currentUser;

    private CarSelectionListener carSelectionListener;

    public CarsController(CarsView view, CarService service, UserResponseDto currentUser) {
        this.view = view;
        this.service = service;
        this.currentUser = currentUser;

        initializeListeners();
        loadCars();
    }

    private void initializeListeners() {
        view.getAgregarButton().addActionListener(e -> handleAdd());
        view.getUpdateButton().addActionListener(e -> handleUpdate());
        view.getBorrarButton().addActionListener(e -> handleDelete());
        view.getClearButton().addActionListener(e -> view.clearFields());

        view.getCarsTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getCarsTable().getSelectedRow() != -1) {
                int selectedRow = view.getCarsTable().getSelectedRow();
                CarResponseDto selectedCar = view.getTableModel().getCarAt(selectedRow);
                view.populateFields(selectedCar);

                if (carSelectionListener != null) {
                    carSelectionListener.onCarSelected(selectedCar);
                }
            }
        });
    }

    public void setCarSelectionListener(CarSelectionListener listener) {
        this.carSelectionListener = listener;
    }

    private void loadCars() {
        view.showLoading(true);

        new Thread(() -> {
            try {
                Future<List<CarResponseDto>> future = service.listCarsAsync(currentUser.getId());
                List<CarResponseDto> cars = future.get();

                SwingUtilities.invokeLater(() -> {
                    view.getTableModel().setCars(cars);
                    view.showLoading(false);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    JOptionPane.showMessageDialog(null, "Error loading cars: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void handleAdd() {
        String make = view.getCarMakeField().getText().trim();
        String model = view.getCarModelField().getText().trim();
        String yearStr = view.getYearTextField().getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            view.showLoading(true);

            new Thread(() -> {
                try {
                    AddCarRequestDto dto = new AddCarRequestDto(make, model, year, currentUser.getId());
                    Future<CarResponseDto> future = service.addCarAsync(dto, currentUser.getId());
                    CarResponseDto result = future.get();

                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        if (result != null) {
                            view.getTableModel().addCar(result);
                            view.clearFields();
                            setChanged();
                            notifyObservers(result);
                            JOptionPane.showMessageDialog(null, "Car added successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add car", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int selectedRow = view.getCarsTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a car to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String make = view.getCarMakeField().getText().trim();
        String model = view.getCarModelField().getText().trim();
        String yearStr = view.getYearTextField().getText().trim();

        if (make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            CarResponseDto selected = view.getTableModel().getCarAt(selectedRow);
            view.showLoading(true);

            new Thread(() -> {
                try {
                    UpdateCarRequestDto dto = new UpdateCarRequestDto(selected.getId(), make, model, year);
                    Future<CarResponseDto> future = service.updateCarAsync(dto, currentUser.getId());
                    CarResponseDto result = future.get();

                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        if (result != null) {
                            view.getTableModel().updateCar(selectedRow, result);
                            view.clearFields();
                            setChanged();
                            notifyObservers(result);
                            JOptionPane.showMessageDialog(null, "Car updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update car", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Year must be a number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = view.getCarsTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a car to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this car?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        CarResponseDto selected = view.getTableModel().getCarAt(selectedRow);
        view.showLoading(true);

        new Thread(() -> {
            try {
                DeleteCarRequestDto dto = new DeleteCarRequestDto(selected.getId());
                Future<Boolean> future = service.deleteCarAsync(dto, currentUser.getId());
                Boolean result = future.get();

                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    if (result) {
                        view.getTableModel().removeCar(selectedRow);
                        view.clearFields();
                        setChanged();
                        notifyObservers(selected);
                        JOptionPane.showMessageDialog(null, "Car deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete car", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    public interface CarSelectionListener {
        void onCarSelected(CarResponseDto car);
    }
}