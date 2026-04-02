package com.example.teste_tinnova_java.controllers;


import com.example.teste_tinnova_java.domain.vehicle.Vehicle;
import com.example.teste_tinnova_java.dto.CreateVehicleDTO;
import com.example.teste_tinnova_java.dto.PatchVehicleDTO;
import com.example.teste_tinnova_java.services.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/veiculos")
public class VehicleController {

    private VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Page<Vehicle>> getVehicles(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) Double minPreco,
            @RequestParam(required = false) Double maxPreco,
            @PageableDefault(size = 10, sort = "year") Pageable pageable
    ) {
        Page<Vehicle> vehicles = this.vehicleService.getVehicles(marca, ano, cor, minPreco, maxPreco, pageable);
        return ResponseEntity.ok(vehicles);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> findVehicleById(@PathVariable String id) {
        Vehicle vehicle = this.vehicleService.findVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody CreateVehicleDTO body) {
        Vehicle vehicle = this.vehicleService.createVehicle(body);
        return ResponseEntity.status(201).body(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity updateVehicle(@RequestBody CreateVehicleDTO body, @PathVariable String id) {
        this.vehicleService.updateVehicle(id, body);
        return ResponseEntity.status(204).build();
    }
@PreAuthorize("hasRole('ADMIN')")
    
    @PatchMapping("/{id}")
    public ResponseEntity patchVehicle(@RequestBody PatchVehicleDTO body, @PathVariable String id) {
        this.vehicleService.patchVehicle(id, body);
        return ResponseEntity.status(204).build();
    }
@PreAuthorize("hasRole('ADMIN')")
    
    @DeleteMapping("/{id}")
    public ResponseEntity deleteVehicle(@PathVariable String id) {
        this.vehicleService.deleteVehicle(id);
        return ResponseEntity.status(204).build();
    }
@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    
    @GetMapping("/relatorios/por-marca")
    public ResponseEntity<HashMap<String, Integer>> getReportsByBrand() {
        HashMap<String, Integer> reportsByBrand = this.vehicleService.getReportsByBrand();
        return ResponseEntity.ok(reportsByBrand);
    }
}
