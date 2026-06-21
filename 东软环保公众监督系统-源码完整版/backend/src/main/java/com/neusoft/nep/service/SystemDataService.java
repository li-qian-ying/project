package com.neusoft.nep.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return result;
    }

    private long countStatus(List<AqiFeedback> rows, String status) { return rows.stream().filter(item -> status.equals(item.getStatus())).count(); }
    private <T> Map<String, Long> group(List<T> rows, Function<T, String> classifier) { return rows.stream().collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.counting())); }
    private boolean sameArea(GridArea area, GridWorker worker) { return area.getProvince().equals(worker.getProvince()) && area.getCity().equals(worker.getCity()); }
    private boolean sameArea(GridArea area, AqiFeedback feedback) { return area.getProvince().equals(feedback.getProvince()) && area.getCity().equals(feedback.getCity()); }
}
