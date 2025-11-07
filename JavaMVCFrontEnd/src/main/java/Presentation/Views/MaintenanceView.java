package Presentation.Views;

import Domain.Dtos.maintenance.MaintenanceResponseDto;
import Presentation.Models.MaintenanceTableModel;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
    private JDateChooser dateChooser;
    private JSpinner timeSpinner;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public MaintenanceView(JFrame parentFrame) {
        tableModel = new MaintenanceTableModel();
        MaintenanceTable.setModel(tableModel);

        TypeComboBox.addItem("ROUTINE");
        TypeComboBox.addItem("REPAIR");
        TypeComboBox.addItem("MOD");

        initializeDateTimePickers();

        loadingOverlay = new LoadingOverlay(parentFrame);
    }

    private void initializeDateTimePickers() {
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date());


        SpinnerDateModel timeModel = new SpinnerDateModel();
        timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm:ss");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());

        DatePanel.setLayout(new BoxLayout(DatePanel, BoxLayout.X_AXIS));
        DatePanel.add(new JLabel("Date:"));
        DatePanel.add(Box.createHorizontalStrut(5));
        DatePanel.add(dateChooser);
        DatePanel.add(Box.createHorizontalStrut(10));
        DatePanel.add(new JLabel("Time:"));
        DatePanel.add(Box.createHorizontalStrut(5));
        DatePanel.add(timeSpinner);
    }

    public void showLoading(boolean visible) {
        loadingOverlay.show(visible);
    }

    public MaintenanceTableModel getTableModel() {
        return tableModel;
    }

    public JPanel getContentPanel() {
        return ContentPanel;
    }

    public JTable getMaintenanceTable() {
        return MaintenanceTable;
    }

    public JButton getAddButton() {
        return AddButton;
    }

    public JButton getDeleteButton() {
        return DeleteButton;
    }

    public JButton getUpdateButton() {
        return UpdateButton;
    }

    public JButton getClearButton() {
        return ClearButton;
    }

    public JTextField getDescriptionField() {
        return DescriptionField;
    }

    public JComboBox<String> getTypeComboBox() {
        return TypeComboBox;
    }

    public Long getCurrentCarId() {
        return currentCarId;
    }

    public void setCurrentCarId(Long carId) {
        this.currentCarId = carId;
        CarIdLabel.setText("Car ID: " + (carId != null ? carId : "None"));
    }

    public void clearFields() {
        DescriptionField.setText("");
        TypeComboBox.setSelectedIndex(0);
        dateChooser.setDate(new Date());
        timeSpinner.setValue(new Date());
        MaintenanceTable.clearSelection();
    }

    public void populateFields(MaintenanceResponseDto maintenance) {
        DescriptionField.setText(maintenance.getDescription());
        TypeComboBox.setSelectedItem(maintenance.getType());

        try {
            LocalDateTime dateTime = LocalDateTime.parse(maintenance.getMaintenanceDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            dateChooser.setDate(date);
            timeSpinner.setValue(date);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
    }

    public String getSelectedType() {
        return (String) TypeComboBox.getSelectedItem();
    }

    public String getFormattedDateTime() {
        Date selectedDate = dateChooser.getDate();
        Date selectedTime = (Date) timeSpinner.getValue();

        if (selectedDate == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String dateStr = dateFormat.format(selectedDate);
        String timeStr = timeFormat.format(selectedTime);

        return dateStr + " " + timeStr;
    }
}