package Presentation.Views;

import Domain.Dtos.maintenance.MaintenanceResponseDto;
import Presentation.Models.MaintenanceTableModel;

import javax.swing.*;
import java.time.format.DateTimeFormatter;

public class MaintenanceView {
    private JPanel ContentPanel;
    private JPanel FormPanel;
    private JTable MaintenanceTable;
    private JScrollPane MaintenanceTableScroll;
    private JButton AddButton;
    private JButton DeleteButton;
    private JButton ClearButton;
    private JButton UpdateButton;
    private JTextField DescriptionField;
    private JComboBox<String> TypeComboBox;
    private JPanel DatePanel;
    private JLabel CarIdLabel;

    private final MaintenanceTableModel tableModel;
    private final LoadingOverlay loadingOverlay;
    private Long currentCarId;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MaintenanceView(JFrame parentFrame) {
        tableModel = new MaintenanceTableModel();
        MaintenanceTable.setModel(tableModel);

        // Initialize ComboBox with maintenance types
        TypeComboBox.addItem("ROUTINE");
        TypeComboBox.addItem("REPAIR");
        TypeComboBox.addItem("MOD");

        loadingOverlay = new LoadingOverlay(parentFrame);
    }

    public void showLoading(boolean visible) {
        loadingOverlay.show(visible);
    }

    // --- Getters ---
    public MaintenanceTableModel getTableModel() { return tableModel; }
    public JPanel getContentPanel() { return ContentPanel; }
    public JTable getMaintenanceTable() { return MaintenanceTable; }
    public JButton getAddButton() { return AddButton; }
    public JButton getDeleteButton() { return DeleteButton; }
    public JButton getUpdateButton() { return UpdateButton; }
    public JButton getClearButton() { return ClearButton; }
    public JTextField getDescriptionField() { return DescriptionField; }
    public JComboBox<String> getTypeComboBox() { return TypeComboBox; }
    public JPanel getDateField() { return DatePanel; }
    public Long getCurrentCarId() { return currentCarId; }

    public void setCurrentCarId(Long carId) {
        this.currentCarId = carId;
        CarIdLabel.setText("Car ID: " + (carId != null ? carId : "None"));
    }

    // --- Helper Methods ---
    public void clearFields() {
        DescriptionField.setText("");
        TypeComboBox.setSelectedIndex(0);
        // need to use calendar
        MaintenanceTable.clearSelection();
    }

    public void populateFields(MaintenanceResponseDto maintenance) {
        DescriptionField.setText(maintenance.getDescription());
        TypeComboBox.setSelectedItem(maintenance.getType());
        // need to use calendar
    }

    public String getSelectedType() {
        return (String) TypeComboBox.getSelectedItem();
    }
}