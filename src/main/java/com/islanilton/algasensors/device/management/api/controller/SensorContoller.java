package com.islanilton.algasensors.device.management.api.controller;

import com.islanilton.algasensors.device.management.api.model.SensorInput;
import com.islanilton.algasensors.device.management.api.model.SensorOutput;
import com.islanilton.algasensors.device.management.common.IdGenerator;
import com.islanilton.algasensors.device.management.domain.model.Sensor;
import com.islanilton.algasensors.device.management.domain.model.SensorId;
import com.islanilton.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorContoller {

    private final SensorRepository sensorRepository;

    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable){
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        return sensors.map(this::toModel);
    }

    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor =  getSensorDB(sensorId);
        return toModel(sensor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(false)
                .build();
        sensor = sensorRepository.saveAndFlush(sensor);
        return toModel(sensor);
    }

    @PutMapping("{sensorId}")
    @ResponseStatus(HttpStatus.OK)
    public SensorOutput update(@PathVariable TSID sensorId,@RequestBody SensorInput input) {
        getSensorDB(sensorId);
        Sensor sensor = Sensor.builder()
                .id(new SensorId(sensorId))
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(input.getEnabled())
                .build();
        sensor = sensorRepository.saveAndFlush(sensor);
        return toModel(sensor);
    }

    @DeleteMapping("{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        Sensor sensor = getSensorDB(sensorId);
        sensorRepository.delete(sensor);
    }

    @PutMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable TSID sensorId) {
        Sensor sensor = getSensorDB(sensorId);
        sensor.setEnabled(true);
        sensorRepository.saveAndFlush(sensor);
    }

    @DeleteMapping("{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivate(@PathVariable TSID sensorId) {
        Sensor sensor = getSensorDB(sensorId);
        sensor.setEnabled(false);
        sensorRepository.saveAndFlush(sensor);
    }

    private SensorOutput toModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .name(sensor.getName())
                .protocol(sensor.getProtocol())
                .ip(sensor.getIp())
                .location(sensor.getLocation())
                .model(sensor.getModel())
                .enabled(sensor.getEnabled())
                .build();
    }

    private Sensor getSensorDB(TSID sensorId) {
        return sensorRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
