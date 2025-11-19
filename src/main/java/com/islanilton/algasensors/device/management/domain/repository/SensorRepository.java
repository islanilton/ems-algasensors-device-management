package com.islanilton.algasensors.device.management.domain.repository;

import com.islanilton.algasensors.device.management.domain.model.Sensor;
import com.islanilton.algasensors.device.management.domain.model.SensorId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, SensorId> {
}
