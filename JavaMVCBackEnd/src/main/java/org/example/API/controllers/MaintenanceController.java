package org.example.API.controllers;

import com.google.gson.Gson;
import org.example.DataAccess.services.MaintenanceService;
import org.example.Domain.dtos.RequestDto;
import org.example.Domain.dtos.ResponseDto;
import org.example.Domain.dtos.maintenance.*;
import org.example.Domain.models.Maintenance;
import org.example.Domain.models.MaintenanceType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final Gson gson = new Gson();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public ResponseDto route(RequestDto request) {
        try {
            switch (request.getRequest()) {
                case "add":
                    return handleAddMaintenance(request);
                case "update":
                    return handleUpdateMaintenance(request);
                case "delete":
                    return handleDeleteMaintenance(request);
                case "list":
                    return handleListMaintenances(request);
                case "get":
                    return handleGetMaintenance(request);
                default:
                    return new ResponseDto(false, "Unknown request: " + request.getRequest(), null);
            }
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage(), null);
        }
    }

    // --- ADD MAINTENANCE ---
    private ResponseDto handleAddMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            AddMaintenanceRequestDto dto = gson.fromJson(request.getData(), AddMaintenanceRequestDto.class);

            MaintenanceType type = MaintenanceType.valueOf(dto.getType().toUpperCase());
            LocalDateTime maintenanceDate = LocalDateTime.parse(dto.getMaintenanceDate(), DATE_FORMATTER);

            Maintenance maintenance = maintenanceService.createMaintenance(
                    dto.getDescription(),
                    type,
                    maintenanceDate,
                    dto.getCarId()
            );

            MaintenanceResponseDto response = toResponseDto(maintenance);
            return new ResponseDto(true, "Maintenance added successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleAddMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error adding maintenance: " + e.getMessage(), null);
        }
    }

    // --- UPDATE MAINTENANCE ---
    private ResponseDto handleUpdateMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            UpdateMaintenanceRequestDto dto = gson.fromJson(request.getData(), UpdateMaintenanceRequestDto.class);

            MaintenanceType type = MaintenanceType.valueOf(dto.getType().toUpperCase());
            LocalDateTime maintenanceDate = LocalDateTime.parse(dto.getMaintenanceDate(), DATE_FORMATTER);

            Maintenance updated = maintenanceService.updateMaintenance(
                    dto.getId(),
                    dto.getDescription(),
                    type,
                    maintenanceDate
            );

            if (updated == null) {
                return new ResponseDto(false, "Maintenance not found", null);
            }

            MaintenanceResponseDto response = toResponseDto(updated);
            return new ResponseDto(true, "Maintenance updated successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleUpdateMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error updating maintenance: " + e.getMessage(), null);
        }
    }

    // --- DELETE MAINTENANCE ---
    private ResponseDto handleDeleteMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            DeleteMaintenanceRequestDto dto = gson.fromJson(request.getData(), DeleteMaintenanceRequestDto.class);
            boolean deleted = maintenanceService.deleteMaintenance(dto.getId());

            if (!deleted) {
                return new ResponseDto(false, "Maintenance not found or could not be deleted", null);
            }

            return new ResponseDto(true, "Maintenance deleted successfully", null);
        } catch (Exception e) {
            System.out.println("Error in handleDeleteMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error deleting maintenance: " + e.getMessage(), null);
        }
    }

    // --- LIST MAINTENANCES BY CAR ---
    private ResponseDto handleListMaintenances(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            // Expecting carId in the data field
            Long carId = gson.fromJson(request.getData(), Long.class);

            List<Maintenance> maintenances = maintenanceService.getAllMaintenanceByCarId(carId);
            List<MaintenanceResponseDto> maintenanceDtos = maintenances.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());

            ListMaintenanceResponseDto responseDto = new ListMaintenanceResponseDto(maintenanceDtos);
            return new ResponseDto(true, "Maintenances retrieved successfully", gson.toJson(responseDto));
        } catch (Exception e) {
            System.out.println("Error in handleListMaintenances: " + e.getMessage());
            return new ResponseDto(false, "Error listing maintenances: " + e.getMessage(), null);
        }
    }

    // --- GET SINGLE MAINTENANCE ---
    private ResponseDto handleGetMaintenance(RequestDto request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return new ResponseDto(false, "Unauthorized", null);
            }

            Long id = gson.fromJson(request.getData(), Long.class);
            Maintenance maintenance = maintenanceService.getMaintenanceById(id);

            if (maintenance == null) {
                return new ResponseDto(false, "Maintenance not found", null);
            }

            MaintenanceResponseDto response = toResponseDto(maintenance);
            return new ResponseDto(true, "Maintenance retrieved successfully", gson.toJson(response));
        } catch (Exception e) {
            System.out.println("Error in handleGetMaintenance: " + e.getMessage());
            return new ResponseDto(false, "Error getting maintenance: " + e.getMessage(), null);
        }
    }

    // --- HELPER: Convert Maintenance to DTO ---
    private MaintenanceResponseDto toResponseDto(Maintenance maintenance) {
        return new MaintenanceResponseDto(
                maintenance.getId(),
                maintenance.getDescription(),
                maintenance.getType().toString(),
                maintenance.getMaintenanceDate().format(DATE_FORMATTER),
                maintenance.getCarMaintenance().getId(),
                maintenance.getCreatedAt() != null ? maintenance.getCreatedAt().toString() : null,
                maintenance.getUpdatedAt() != null ? maintenance.getUpdatedAt().toString() : null
        );
    }
}