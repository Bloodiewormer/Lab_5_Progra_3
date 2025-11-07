package Services;

import Domain.Dtos.RequestDto;
import Domain.Dtos.ResponseDto;
import Domain.Dtos.maintenance.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MaintenanceService extends BaseService {
    private final ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

    public MaintenanceService(String host, int port) {
        super(host, port);
    }

    public Future<MaintenanceResponseDto> addMaintenanceAsync(AddMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenance", "add", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            return gson.fromJson(response.getData(), MaintenanceResponseDto.class);
        });
    }

    public Future<MaintenanceResponseDto> updateMaintenanceAsync(UpdateMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenance", "update", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            return gson.fromJson(response.getData(), MaintenanceResponseDto.class);
        });
    }

    public Future<Boolean> deleteMaintenanceAsync(DeleteMaintenanceRequestDto dto, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenance", "delete", gson.toJson(dto), userId.toString());
            ResponseDto response = sendRequest(request);
            return response.isSuccess();
        });
    }

    public Future<List<MaintenanceResponseDto>> listMaintenancesAsync(Long carId, Long userId) {
        return executor.submit(() -> {
            RequestDto request = new RequestDto("Maintenance", "list", gson.toJson(carId), userId.toString());
            ResponseDto response = sendRequest(request);
            if (!response.isSuccess()) return null;
            ListMaintenanceResponseDto listResponse = gson.fromJson(response.getData(), ListMaintenanceResponseDto.class);
            return listResponse.getMaintenances();
        });
    }
}