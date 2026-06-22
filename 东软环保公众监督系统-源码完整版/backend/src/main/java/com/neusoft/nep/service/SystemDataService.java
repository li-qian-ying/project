package com.neusoft.nep.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemDataService {
    private final GridWorkerMapper workerMapper;
    private final GridAreaMapper areaMapper;
    private final AqiFeedbackMapper feedbackMapper;
    private final AqiMeasurementMapper measurementMapper;
    private final AqiLevelService levelService;

    public List<Map<String, Object>> workers() {
        List<AqiFeedback> feedbacks = feedbackMapper.selectList(null);
        return workerMapper.selectList(Wrappers.<GridWorker>lambdaQuery().orderByAsc(GridWorker::getWorkerCode)).stream().map(worker -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", worker.getId()); row.put("code", worker.getWorkerCode()); row.put("name", worker.getRealName());
            row.put("province", worker.getProvince()); row.put("city", worker.getCity()); row.put("status", worker.getWorkStatus());
            row.put("taskCount", feedbacks.stream().filter(item -> worker.getId().equals(item.getAssignedWorkerId())).count());
            return row;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> areas() {
        List<AqiFeedback> feedbacks = feedbackMapper.selectList(null);
        List<GridWorker> workers = workerMapper.selectList(null);
        return areaMapper.selectList(Wrappers.<GridArea>lambdaQuery().eq(GridArea::getEnabled, true).orderByAsc(GridArea::getProvince, GridArea::getCity)).stream().map(area -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", area.getId()); row.put("province", area.getProvince()); row.put("city", area.getCity()); row.put("enabled", area.getEnabled());
            row.put("workerCount", workers.stream().filter(worker -> sameArea(area, worker)).count());
            row.put("feedbackCount", feedbacks.stream().filter(item -> sameArea(area, item)).count());
            return row;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> stats() {
        List<AqiFeedback> feedbacks = feedbackMapper.selectList(null);
        List<AqiMeasurement> measurements = measurementMapper.selectList(null);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("feedbackTotal", feedbacks.size());
        result.put("doneTotal", countStatus(feedbacks, "已完成")); result.put("waitingTotal", countStatus(feedbacks, "待指派"));
        result.put("doingTotal", countStatus(feedbacks, "已指派"));
        result.put("badTotal", measurements.stream().filter(item -> levelService.levelIndex(item.getFinalAqiLevel()) >= 2).count());
        result.put("goodTotal", measurements.stream().filter(item -> levelService.levelIndex(item.getFinalAqiLevel()) < 2).count());
        result.put("provinceCoverage", areas().stream().map(row -> row.get("province")).distinct().count());
        result.put("cityCoverage", areas().size());
        result.put("status", group(feedbacks, AqiFeedback::getStatus)); result.put("provinces", group(feedbacks, AqiFeedback::getProvince));
        result.put("levels", group(measurements, AqiMeasurement::getFinalAqiLevel));
        result.put("trend", trend(feedbacks));
        result.put("pollutants", pollutantByProvince(feedbacks, measurements));
        result.put("realTimeDetectionTotal", measurements.size());
        return result;
    }

    private List<Map<String, Object>> pollutantByProvince(List<AqiFeedback> feedbacks, List<AqiMeasurement> measurements) {
        Map<Long, String> provinceByFeedback = feedbacks.stream().collect(Collectors.toMap(
                AqiFeedback::getId, AqiFeedback::getProvince, (first, second) -> first));
        Map<String, int[]> grouped = new TreeMap<>();
        feedbacks.forEach(item -> grouped.computeIfAbsent(item.getProvince(), key -> new int[4]));
        measurements.forEach(item -> {
            String province = provinceByFeedback.get(item.getFeedbackId());
            if (province == null) return;
            int[] values = grouped.computeIfAbsent(province, key -> new int[4]);
            if (levelService.levelIndex(item.getSo2Level()) >= 2) values[0]++;
            if (levelService.levelIndex(item.getCoLevel()) >= 2) values[1]++;
            if (levelService.levelIndex(item.getPm25Level()) >= 2) values[2]++;
            if (levelService.levelIndex(item.getFinalAqiLevel()) >= 2) values[3]++;
        });
        return grouped.entrySet().stream().map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("province", entry.getKey());
            row.put("so2", entry.getValue()[0]);
            row.put("co", entry.getValue()[1]);
            row.put("pm25", entry.getValue()[2]);
            row.put("aqi", entry.getValue()[3]);
            return row;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> trend(List<AqiFeedback> feedbacks) {
        Map<String, long[]> grouped = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        feedbacks.stream().filter(item -> item.getCreateTime() != null).forEach(item -> {
            String day = item.getCreateTime().format(formatter);
            long[] values = grouped.computeIfAbsent(day, key -> new long[2]);
            values[0]++;
            if ("已完成".equals(item.getStatus())) values[1]++;
        });
        int skip = Math.max(0, grouped.size() - 7);
        return grouped.entrySet().stream().skip(skip).map(entry -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("day", entry.getKey());
            row.put("received", entry.getValue()[0]);
            row.put("done", entry.getValue()[1]);
            return row;
        }).collect(Collectors.toList());
    }

    private long countStatus(List<AqiFeedback> rows, String status) { return rows.stream().filter(item -> status.equals(item.getStatus())).count(); }
    private <T> Map<String, Long> group(List<T> rows, Function<T, String> classifier) { return rows.stream().collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.counting())); }
    private boolean sameArea(GridArea area, GridWorker worker) { return area.getProvince().equals(worker.getProvince()) && area.getCity().equals(worker.getCity()); }
    private boolean sameArea(GridArea area, AqiFeedback feedback) { return area.getProvince().equals(feedback.getProvince()) && area.getCity().equals(feedback.getCity()); }
}
