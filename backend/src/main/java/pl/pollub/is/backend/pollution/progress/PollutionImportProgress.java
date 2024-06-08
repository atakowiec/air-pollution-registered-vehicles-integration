package pl.pollub.is.backend.pollution.progress;

import lombok.Getter;
import pl.pollub.is.backend.progress.model.Progress;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PollutionImportProgress extends Progress {
    public final static String PROGRESS_KEY = "pollution_xlsx_import";

    public Map<String, Map<String, Object>> indicatorsStatus;

    public PollutionImportProgress() {
        super(PROGRESS_KEY);
    }

    @Override
    public void clear() {
        super.clear();

        this.indicatorsStatus = new HashMap<>();
        setProgressData("indicatorsStatus", this.indicatorsStatus);

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

    public void addRead(long read) {
        setRead(getRead() + read);
    }

    public long getRead() {
        return (long) getProgressData().getOrDefault("read", 0);
    }

    public void setSaved(long saved) {
        setProgressData("saved", saved);
    }

    public void addSaved(long saved) {
        setSaved(getSaved() + saved);
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

    public void addIndicatorRead(String indicator, long read) {
        Map<String, Object> indicatorData = indicatorsStatus.computeIfAbsent(indicator, _ -> new HashMap<>());
        Number currentRead = (Number) indicatorData.getOrDefault("read", 0);
        indicatorData.put("read", currentRead.longValue() + read);
    }

    public void addIndicatorSaved(String indicator, long saved) {
        Map<String, Object> indicatorData = indicatorsStatus.computeIfAbsent(indicator, _ -> new HashMap<>());
        Number currentSaved = (Number) indicatorData.getOrDefault("saved", 0);
        indicatorData.put("saved", currentSaved.longValue() + saved);
    }

    public void setIndicatorTotal(String indicator, long total) {
        Map<String, Object> indicatorData = indicatorsStatus.computeIfAbsent(indicator, _ -> new HashMap<>());
        indicatorData.put("total", total);
    }

    public void setIndicatorReadDate(String indicator) {
        Map<String, Object> indicatorData = indicatorsStatus.computeIfAbsent(indicator, _ -> new HashMap<>());
        indicatorData.put("readTime", System.currentTimeMillis());
    }

    public void setIndicatorSavedDate(String indicator) {
        Map<String, Object> indicatorData = indicatorsStatus.computeIfAbsent(indicator, _ -> new HashMap<>());
        indicatorData.put("savedTime", System.currentTimeMillis());
    }
}
