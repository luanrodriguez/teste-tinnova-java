package com.example.teste_tinnova_java.services;

import com.example.teste_tinnova_java.client.DolarApiClient;
import com.example.teste_tinnova_java.domain.vehicle.Vehicle;
import com.example.teste_tinnova_java.dto.CreateVehicleDTO;
import com.example.teste_tinnova_java.dto.PatchVehicleDTO;
import com.example.teste_tinnova_java.repositories.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DolarApiClient dolarApiClient;


    public VehicleService(VehicleRepository vehicleRepository, DolarApiClient dolarApiClient) {
        this.vehicleRepository = vehicleRepository;
        this.dolarApiClient = dolarApiClient;
    }

    public Page<Vehicle> getVehicles(
            String marca,
            Integer ano,
            String cor,
            Double minPreco,
            Double maxPreco,
            Pageable pageable
    ) {
        Specification<Vehicle> spec = (root, query, cb) -> cb.conjunction();

        if (marca != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("brand"), marca));
        }

        if (ano != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("year"), ano));
        }

        if (cor != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("color"), cor));
        }

        if (minPreco != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), minPreco));
        }

        if (maxPreco != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), maxPreco));
        }
        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("deleted")));
        return this.vehicleRepository.findAll(spec, pageable);
    }

    public Vehicle findVehicleById(String id) {
        return this.vehicleRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle not found"));
    }

    public Vehicle createVehicle(CreateVehicleDTO vehicleDTO) {
        Optional<Vehicle> vehicleWithPlate = this.vehicleRepository.findByPlateAndDeletedIsFalse(vehicleDTO.placa());
        if(vehicleWithPlate.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "this plate already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(vehicleDTO.marca());
        vehicle.setYear(vehicleDTO.ano());
        vehicle.setColor(vehicleDTO.cor());
        vehicle.setPlate(vehicleDTO.placa());
        vehicle.setPrice(this.dolarApiClient.convertBrlToUsd(vehicleDTO.preco()));

        this.vehicleRepository.save(vehicle);
        return vehicle;
    }

    public void updateVehicle(String id, CreateVehicleDTO vehicleDTO) {
        Vehicle vehicle = this.vehicleRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle not found"));
        Optional<Vehicle> vehicleWithPlate = this.vehicleRepository.findByPlateAndDeletedIsFalse(vehicleDTO.placa());
        if(vehicleWithPlate.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "this plate already exists");
        }

        vehicle.setBrand(vehicleDTO.marca());
        vehicle.setYear(vehicleDTO.ano());
        vehicle.setColor(vehicleDTO.cor());
        vehicle.setPlate(vehicleDTO.placa());
        vehicle.setPrice(this.dolarApiClient.convertBrlToUsd(vehicleDTO.preco()));

        this.vehicleRepository.save(vehicle);
    }

    public void patchVehicle(String id, PatchVehicleDTO vehicleDTO) {
        Vehicle vehicle = this.vehicleRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle not found"));

        if(vehicleDTO.marca() != null) {
            vehicle.setBrand(vehicleDTO.marca());
        }
        if(vehicleDTO.ano() != null) {
            vehicle.setYear(vehicleDTO.ano());
        }
        if(vehicleDTO.preco() != null) {
            vehicle.setPrice(this.dolarApiClient.convertBrlToUsd(vehicleDTO.preco()));
        }
        if(vehicleDTO.cor() != null) {
            vehicle.setColor(vehicleDTO.cor());
        }
        if(vehicleDTO.placa() != null) {
            Optional<Vehicle> vehicleWithPlate = this.vehicleRepository.findByPlateAndDeletedIsFalse(vehicleDTO.placa());
            if(vehicleWithPlate.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "this plate already exists");
            }

            vehicle.setPlate(vehicleDTO.placa());
        }

        this.vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String id) {
        Vehicle vehicle = this.vehicleRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vehicle not found"));

        vehicle.setDeleted(true);
        this.vehicleRepository.save(vehicle);
    }

    public HashMap<String, Integer> getReportsByBrand() {
        List<Vehicle> vehicles = this.vehicleRepository.findByDeletedIsFalse();
        HashMap<String, Integer> reportsByBrand = new HashMap<>();
        for(Vehicle vehicle : vehicles) {
            String brand = vehicle.getBrand();
            reportsByBrand.merge(brand, 1, Integer::sum);
        }

        return reportsByBrand;
    }
}
