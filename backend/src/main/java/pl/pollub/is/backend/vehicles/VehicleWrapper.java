package pl.pollub.is.backend.vehicles;

import lombok.Data;

import java.util.List;

@Data
public class VehicleWrapper {
    private final Vehicle vehicle = new Vehicle();
    private final String line;
    private final String[] columnNames;
    private final List<String> values;
}
