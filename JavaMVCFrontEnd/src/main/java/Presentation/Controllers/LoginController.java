package Presentation.Controllers;

import Domain.Dtos.auth.UserResponseDto;
import Presentation.IObserver;
import Presentation.Views.CarsView;
import Presentation.Views.LoginView;
import Presentation.Views.MaintenanceView;
import Services.AuthService;
import Services.CarService;
import Services.MaintenanceService;
import Services.MessageService;
import Utilities.EventType;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class LoginController {
    private final LoginView view;
    private final AuthService service;
    private final List<IObserver> observers = new ArrayList<>();

    public LoginController(LoginView view, AuthService service) {
        this.view = view;
        this.service = service;

        view.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            notifyObservers(EventType.DELETED, "Username and password are required");
            return;
        }

        view.showLoading(true);

        new Thread(() -> {
            try {
                Future<UserResponseDto> future = service.login(username, password);
                UserResponseDto user = future.get();

                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    if (user != null) {
                        notifyObservers(EventType.CREATED, user);
                        openDashboard(user);
                        view.dispose();
                    } else {
                        notifyObservers(EventType.DELETED, "Invalid credentials");
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    view.showLoading(false);
                    notifyObservers(EventType.DELETED, "Login failed: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void openDashboard(UserResponseDto user) {
        JFrame dashboardFrame = new JFrame("Car Management System - " + user.getUsername());
        dashboardFrame.setSize(900, 650);
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        CarService carService = new CarService("localhost", 7000);
        MaintenanceService maintenanceService = new MaintenanceService("localhost", 7000);

        CarsView carsView = new CarsView(dashboardFrame);
        CarsController carsController = new CarsController(carsView, carService, user);

        MaintenanceView maintenanceView = new MaintenanceView(dashboardFrame);
        MaintenanceController maintenanceController = new MaintenanceController(maintenanceView, maintenanceService, user.getId());

        carsController.setCarSelectionListener(car -> {
            maintenanceController.loadMaintenancesForCar(car.getId());
            tabbedPane.setSelectedIndex(1);
        });

        tabbedPane.addTab("Cars", carsView.getContentPanel());
        tabbedPane.addTab("Maintenance", maintenanceView.getContentPanel());

        dashboardFrame.add(tabbedPane);
        dashboardFrame.setVisible(true);

        try {
            MessageService messageService = new MessageService("localhost", 7001, message -> {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(dashboardFrame, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
                });
            });
            messageService.connect();
        } catch (IOException ex) {
            System.err.println("Could not connect to message service: " + ex.getMessage());
        }
    }

    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(EventType eventType, Object data) {
        for (IObserver observer : observers) {
            observer.update(eventType, data);
        }
    }
}