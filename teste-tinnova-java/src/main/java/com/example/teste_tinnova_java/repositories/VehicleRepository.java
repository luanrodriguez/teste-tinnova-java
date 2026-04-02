package com.example.teste_tinnova_java.repositories;

import com.example.teste_tinnova_java.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByIdAndDeletedIsFalse(String id);
    List<Vehicle> findByDeletedIsFalse();
    Optional<Vehicle> findByPlateAndDeletedIsFalse(String plate);
}
