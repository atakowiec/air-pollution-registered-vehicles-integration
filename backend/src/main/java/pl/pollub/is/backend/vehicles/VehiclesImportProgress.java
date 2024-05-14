package pl.pollub.is.backend.vehicles;

import lombok.Getter;
import pl.pollub.is.backend.progress.model.Progress;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class VehiclesImportProgress extends Progress {
    public final static String PROGRESS_KEY = "vehicles_csv_import";
    public long saved = 0;

    public long readErrors = 0;
    public List<Map<String, Object>> readErrorsList;

    public long saveErrors = 0;
    public List<String> saveErrorsList;

    public VehiclesImportProgress() {
        super(PROGRESS_KEY);
    }

    @Override
    public void clear() {
        super.clear();

        this.readErrorsList = new LinkedList<>();
        this.readErrors = 0;
        setProgressData("readErrors", 0);
        setProgressData("readErrorsList", this.readErrorsList);

        this.saveErrorsList = new LinkedList<>();
        this.saveErrors = 0;
        setProgressData("saveErrors", 0);
        setProgressData("saveErrorsList", this.saveErrorsList);

        setDataLoaded(false);
        setTotal(0);
        setRead(0);
        setSaved(0);
    }

    public void setDataLoaded(boolean status) {
        setProgressData("loaded", status);
        if (status)
            setProgressData("loadTime", System.currentTimeMillis());
        else
            removeProgressData("loadTime");
    }

    public void setTotal(long total) {
        setProgressData("total", total);
    }

    public void setRead(long read) {
        setProgressData("read", read);
    }

    public void setSaved(long saved) {
        this.saved = saved;
        setProgressData("saved", saved);
    }

    public void addSaved(long saved) {
        this.saved += saved; // in single operation because of concurrent access
        setProgressData("saved", this.saved);
    }

    public void setStartDate() {
        setProgressData("startTime", System.currentTimeMillis());
    }

    public void setEndDate() {
        setProgressData("endTime", System.currentTimeMillis());
    }

    public void setReadDate() {
        setProgressData("readTime", System.currentTimeMillis());
    }


    public long getSaved() {
        return (long) getProgressData().getOrDefault("saved", 0);
    }

    public long getTotal() {
        return (long) getProgressData().getOrDefault("total", 0);
    }

    public void addReadError(String vehicleId, String errorMessage, Map<String, String> columnData, String line) {
        readErrors++;
        setProgressData("readErrors", readErrors);

        if (readErrors > 100)
            return;

        Map<String, Object> error = Map.of("vehicleId", vehicleId, "errorMessage", errorMessage, "columnData", columnData, "line", line);
        readErrorsList.add(error);
    }

    public void addSaveError(String errorMessage) {
        saveErrors++;
        setProgressData("saveErrors", saveErrors);

        if (saveErrors > 100)
            return;

        saveErrorsList.add(errorMessage);
    }
}
