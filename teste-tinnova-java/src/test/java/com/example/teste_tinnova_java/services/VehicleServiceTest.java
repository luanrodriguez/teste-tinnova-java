package com.example.teste_tinnova_java.services;

import com.example.teste_tinnova_java.client.DolarApiClient;
import com.example.teste_tinnova_java.domain.vehicle.Vehicle;
import com.example.teste_tinnova_java.dto.CreateVehicleDTO;
import com.example.teste_tinnova_java.dto.PatchVehicleDTO;
import com.example.teste_tinnova_java.repositories.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DolarApiClient dolarApiClient;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    @DisplayName("Testa se todos os veículos são listados ao nao enviar parametros")
    public void testListVehicles() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        Page<Vehicle> vehicles = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vehicles);

        Page<Vehicle> result = vehicleService.getVehicles(null, null, null, null, null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Testa se o veiculo é listado ao enviar parametros")
    public void testListVehiclesWithFillters() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        Page<Vehicle> vehicles = new PageImpl<>(List.of(vehicle));
        when(vehicleRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vehicles);

        Page<Vehicle> result = vehicleService.getVehicles("Honda", 2000, "preto", 40000.00, 46000.00, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Testa se o veiculo é criado")
    public void testCreateVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        CreateVehicleDTO dto = new CreateVehicleDTO("Honda", "abc1234", "preto", 2000, 25000.00);

        when(vehicleRepository.findByPlateAndDeletedIsFalse("abc1234")).thenReturn(Optional.empty());
        when(dolarApiClient.convertBrlToUsd(25000.00)).thenReturn(5000.0);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = vehicleService.createVehicle(dto);

        assertEquals("abc1234", result.getPlate());
        assertEquals("Honda", result.getBrand());
        assertEquals(5000.0, result.getPrice());
        assertEquals(2000, result.getYear());
        assertEquals("preto", result.getColor());
    }

    @Test
    @DisplayName("Testa se o veiculo não é criado ao ter a mesma placa")
    public void testCreateVehicleWithSamePlate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        CreateVehicleDTO dto = new CreateVehicleDTO("Honda", "abc1234", "preto", 2000, 25000.00);

        when(vehicleRepository.findByPlateAndDeletedIsFalse("abc1234")).thenReturn(Optional.of(vehicle));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.createVehicle(dto);
        });

        assertEquals("409 CONFLICT \"this plate already exists\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo é atualizado ao enviar todos os parametros")
    public void testUpdateVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        CreateVehicleDTO dto = new CreateVehicleDTO("Toyota", "def5678", "branco", 2023, 50000.00);

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByPlateAndDeletedIsFalse("def5678")).thenReturn(Optional.empty());
        when(dolarApiClient.convertBrlToUsd(50000.00)).thenReturn(10000.0);

        vehicleService.updateVehicle("123", dto);

        verify(vehicleRepository, times(1)).save(vehicle);
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("def5678", vehicle.getPlate());
        assertEquals("branco", vehicle.getColor());
        assertEquals(2023, vehicle.getYear());
    }

    @Test
    @DisplayName("Testa se o veiculo não é atualizado quando a placa já existe")
    public void testUpdateVehicleWithSamePlate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        Vehicle otherVehicle = new Vehicle();
        otherVehicle.setId("456");
        otherVehicle.setPlate("def5678");

        CreateVehicleDTO dto = new CreateVehicleDTO("Toyota", "def5678", "branco", 2023, 50000.00);

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByPlateAndDeletedIsFalse("def5678")).thenReturn(Optional.of(otherVehicle));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.updateVehicle("123", dto);
        });

        assertEquals("409 CONFLICT \"this plate already exists\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo não é atualizado quando não encontrado")
    public void testUpdateVehicleNotFound() {
        CreateVehicleDTO dto = new CreateVehicleDTO("Toyota", "def5678", "branco", 2023, 50000.00);

        when(vehicleRepository.findByIdAndDeletedIsFalse("999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.updateVehicle("999", dto);
        });

        assertEquals("404 NOT_FOUND \"vehicle not found\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo é atualizado parcialmente")
    public void testPatchVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        PatchVehicleDTO dto = new PatchVehicleDTO("Toyota", null, "branco", null, null);

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));

        vehicleService.patchVehicle("123", dto);

        verify(vehicleRepository, times(1)).save(vehicle);
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("branco", vehicle.getColor());
        assertEquals("abc1234", vehicle.getPlate());
        assertEquals(45000.00, vehicle.getPrice());
    }

    @Test
    @DisplayName("Testa se o veiculo não é atualizado parcialmente quando a placa já existe")
    public void testPatchVehicleWithSamePlate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        Vehicle otherVehicle = new Vehicle();
        otherVehicle.setId("456");
        otherVehicle.setPlate("def5678");

        PatchVehicleDTO dto = new PatchVehicleDTO(null, "def5678", null, null, null);

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.findByPlateAndDeletedIsFalse("def5678")).thenReturn(Optional.of(otherVehicle));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.patchVehicle("123", dto);
        });

        assertEquals("409 CONFLICT \"this plate already exists\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo não é atualizado parcialmente quando não encontrado")
    public void testPatchVehicleNotFound() {
        PatchVehicleDTO dto = new PatchVehicleDTO("Toyota", null, null, null, null);

        when(vehicleRepository.findByIdAndDeletedIsFalse("999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.patchVehicle("999", dto);
        });

        assertEquals("404 NOT_FOUND \"vehicle not found\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo é recuperado por id")
    public void testFindVehicleById() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.findVehicleById("123");

        assertEquals("123", result.getId());
        assertEquals("abc1234", result.getPlate());
        assertEquals("Honda", result.getBrand());
    }

    @Test
    @DisplayName("Testa se o veiculo não é encontrado por id")
    public void testFindVehicleByIdNotFound() {
        when(vehicleRepository.findByIdAndDeletedIsFalse("999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.findVehicleById("999");
        });

        assertEquals("404 NOT_FOUND \"vehicle not found\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se o veiculo é deletado")
    public void testDeleteVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId("123");
        vehicle.setPlate("abc1234");
        vehicle.setPrice(45000.00);
        vehicle.setYear(2000);
        vehicle.setBrand("Honda");
        vehicle.setColor("preto");
        vehicle.setDeleted(false);

        when(vehicleRepository.findByIdAndDeletedIsFalse("123")).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle("123");

        verify(vehicleRepository, times(1)).save(vehicle);
        assertTrue(vehicle.getDeleted());
    }

    @Test
    @DisplayName("Testa se o veiculo não é deletado quando não encontrado")
    public void testDeleteVehicleNotFound() {
        when(vehicleRepository.findByIdAndDeletedIsFalse("999")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            vehicleService.deleteVehicle("999");
        });

        assertEquals("404 NOT_FOUND \"vehicle not found\"", exception.getMessage());
    }

    @Test
    @DisplayName("Testa se os relatorios por marca sao gerados")
    public void testGetReportsByBrand() {
        Vehicle veiculo1 = new Vehicle();
        veiculo1.setBrand("Honda");

        Vehicle veiculo2 = new Vehicle();
        veiculo2.setBrand("Honda");

        Vehicle veiculo3 = new Vehicle();
        veiculo3.setBrand("Toyota");

        when(vehicleRepository.findByDeletedIsFalse()).thenReturn(List.of(veiculo1, veiculo2, veiculo3));

        HashMap<String, Integer> result = vehicleService.getReportsByBrand();

        assertEquals(2, result.get("Honda"));
        assertEquals(1, result.get("Toyota"));
        assertEquals(2, result.size());
    }
}