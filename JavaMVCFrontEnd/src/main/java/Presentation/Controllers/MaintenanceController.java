package Presentation.Controllers;

import Domain.Dtos.maintenance.*;
import Presentation.Models.MaintenanceTableModel;
import Presentation.Views.MaintenanceView;
import Services.MaintenanceService;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Future;



public class MaintenanceController {
    private final MaintenanceView view;
    private final MaintenanceService service;
    private final Long userId;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public MaintenanceController(MaintenanceView view, MaintenanceService service, Long userId) {
        this.view = view;
        this.service = service;
        this.userId = userId;

        initializeListeners();
    }

    private void initializeListeners() {
        view.getAddButton().addActionListener(e -> handleAdd());
        view.getUpdateButton().addActionListener(e -> handleUpdate());
        view.getDeleteButton().addActionListener(e -> handleDelete());
        view.getClearButton().addActionListener(e -> view.clearFields());

        view.getMaintenanceTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && view.getMaintenanceTable().getSelectedRow() != -1) {
                int selectedRow = view.getMaintenanceTable().getSelectedRow();
                MaintenanceResponseDto maintenance = view.getTableModel().getMaintenanceAt(selectedRow);
                view.populateFields(maintenance);
            }
        });
    }

    public void loadMaintenancesForCar(Long carId) {
        view.setCurrentCarId(carId);
        view.showLoading(true);

        new Thread(() -> {
            try {
                Future<List<MaintenanceResponseDto>> future = service.listMaintenancesAsync(carId, userId);
                List<MaintenanceResponseDto> maintenances = future.get();

                SwingUtilities.invokeLater(() -> {
                    view.getTableModel().setMaintenances(maintenances);
                    view.showLoading(false);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    JOptionPane.showMessageDialog(null, "Error loading maintenances: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void handleAdd() {
        if (view.getCurrentCarId() == null) {
            JOptionPane.showMessageDialog(null, "Please select a car first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String description = view.getDescriptionField().getText().trim();
        String type = view.getSelectedType();
        //String dateStr = view.getDateField().getText().trim();

        if (description.isEmpty() /*|| dateStr.isEmpty()*/) {
            JOptionPane.showMessageDialog(null, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Validate and parse date
            //LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //String isoDate = date.format(DATE_FORMATTER);

            view.showLoading(true);

            new Thread(() -> {
                try {
                    AddMaintenanceRequestDto dto = new AddMaintenanceRequestDto(description, type, isoDate, view.getCurrentCarId());
                    Future<MaintenanceResponseDto> future = service.addMaintenanceAsync(dto, userId);
                    MaintenanceResponseDto result = future.get();

                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        if (result != null) {
                            view.getTableModel().addMaintenance(result);
                            view.clearFields();
                            JOptionPane.showMessageDialog(null, "Maintenance added successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to add maintenance", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid date format. Use: yyyy-MM-dd HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        int selectedRow = view.getMaintenanceTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a maintenance to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String description = view.getDescriptionField().getText().trim();
        String type = view.getSelectedType();
        //String dateStr = view.getDateField().getText().trim();

        if (description.isEmpty() /*|| dateStr.isEmpty()*/) {
            JOptionPane.showMessageDialog(null, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            //LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            //String isoDate = date.format(DATE_FORMATTER);

            MaintenanceResponseDto selected = view.getTableModel().getMaintenanceAt(selectedRow);
            view.showLoading(true);

            new Thread(() -> {
                try {
                   // UpdateMaintenanceRequestDto dto = new UpdateMaintenanceRequestDto(selected.getId(), description, type, /*isoDate*/);
                    Future<MaintenanceResponseDto> future = service.updateMaintenanceAsync(dto, userId);
                    MaintenanceResponseDto result = future.get();

                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        if (result != null) {
                            view.getTableModel().updateMaintenance(selectedRow, result);
                            view.clearFields();
                            JOptionPane.showMessageDialog(null, "Maintenance updated successfully!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update maintenance", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        view.showLoading(false);
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = view.getMaintenanceTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a maintenance to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this maintenance?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        MaintenanceResponseDto selected = view.getTableModel().getMaintenanceAt(selectedRow);
        view.showLoading(true);

        new Thread(() -> {
            try {
                DeleteMaintenanceRequestDto dto = new DeleteMaintenanceRequestDto(selected.getId());
                Future<Boolean> future = service.deleteMaintenanceAsync(dto, userId);
                Boolean result = future.get();

                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    if (result) {
                        view.getTableModel().removeMaintenance(selectedRow);
                        view.clearFields();
                        JOptionPane.showMessageDialog(null, "Maintenance deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete maintenance", "Error", JOptionPane.ERROR_MESSAGE);
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
}