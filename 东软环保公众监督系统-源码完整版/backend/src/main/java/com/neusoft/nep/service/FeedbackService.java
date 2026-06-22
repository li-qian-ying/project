package com.neusoft.nep.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neusoft.nep.dto.AssignRequest;
import com.neusoft.nep.dto.FeedbackCreateRequest;
import com.neusoft.nep.dto.MeasurementRequest;
import com.neusoft.nep.entity.AqiFeedback;
import com.neusoft.nep.entity.AqiMeasurement;
import com.neusoft.nep.entity.GridWorker;
import com.neusoft.nep.mapper.AqiFeedbackMapper;
import com.neusoft.nep.mapper.AqiMeasurementMapper;
import com.neusoft.nep.mapper.GridWorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final AqiFeedbackMapper feedbackMapper;
    private final AqiMeasurementMapper measurementMapper;
    private final GridWorkerMapper workerMapper;
    private final AqiLevelService levelService;

    public List<Map<String, Object>> list(Long supervisorId, Long workerId, String status) {
        List<AqiFeedback> rows = feedbackMapper.selectList(Wrappers.<AqiFeedback>lambdaQuery()
                .eq(supervisorId != null, AqiFeedback::getSupervisorId, supervisorId)
                .eq(workerId != null, AqiFeedback::getAssignedWorkerId, workerId)
                .eq(status != null && !status.trim().isEmpty(), AqiFeedback::getStatus, status)
                .orderByDesc(AqiFeedback::getCreateTime));
        return rows.stream().map(this::toView).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> create(FeedbackCreateRequest request) {
        if (blank(request.getProvince()) || blank(request.getCity()) || blank(request.getAddress()) || blank(request.getEstimate())) {
            throw new IllegalArgumentException("省份、城市、观测地址和预估AQI等级不能为空");
        }
        AqiFeedback feedback = new AqiFeedback();
        feedback.setFeedbackNo(createFeedbackNo());
        feedback.setSupervisorId(request.getSupervisorId());
        feedback.setProvince(request.getProvince());
        feedback.setCity(request.getCity());
        feedback.setAddress(request.getAddress());
        feedback.setEstimatedAqiLevel(request.getEstimate());
        feedback.setDescription(request.getDescription());
        feedback.setStatus("待指派");
        feedback.setCreateTime(LocalDateTime.now());
        feedbackMapper.insert(feedback);
        return toView(feedback);
    }

    @Transactional
    public Map<String, Object> assign(AssignRequest request) {
        if (request.getFeedbackId() == null || request.getWorkerId() == null) {
            throw new IllegalArgumentException("反馈ID和网格员ID不能为空");
        }
        AqiFeedback feedback = requireFeedback(request.getFeedbackId());
        GridWorker worker = workerMapper.selectById(request.getWorkerId());
        if (worker == null) {
            throw new IllegalArgumentException("网格员不存在");
        }
        feedback.setAssignedWorkerId(worker.getId());
        feedback.setStatus("已指派");
        feedback.setAssignType(sameArea(feedback, worker) ? "本地指派" : "异地指派");
        feedback.setAssignTime(LocalDateTime.now());
        feedbackMapper.updateById(feedback);
        return toView(feedback);
    }

    @Transactional
    public Map<String, Object> measure(MeasurementRequest request) {
        if (request.getFeedbackId() == null || request.getWorkerId() == null
                || blank(request.getSo2()) || blank(request.getCo()) || blank(request.getPm25())) {
            throw new IllegalArgumentException("任务、网格员和三项污染物等级不能为空");
        }
        AqiFeedback feedback = requireFeedback(request.getFeedbackId());
        if (feedback.getAssignedWorkerId() == null || !feedback.getAssignedWorkerId().equals(request.getWorkerId())) {
            throw new IllegalArgumentException("该任务未指派给当前网格员");
        }
        String finalLevel = levelService.maximum(request.getSo2(), request.getCo(), request.getPm25());
        AqiMeasurement measurement = measurementMapper.selectOne(Wrappers.<AqiMeasurement>lambdaQuery()
                .eq(AqiMeasurement::getFeedbackId, feedback.getId()));
        if (measurement == null) {
            measurement = new AqiMeasurement();
            measurement.setFeedbackId(feedback.getId());
            measurement.setWorkerId(request.getWorkerId());
            measurement.setFeedbackNo(feedback.getFeedbackNo());
        }
        measurement.setSo2Level(request.getSo2());
        measurement.setCoLevel(request.getCo());
        measurement.setPm25Level(request.getPm25());
        measurement.setFinalAqiLevel(finalLevel);
        measurement.setMeasureTime(LocalDateTime.now());
        if (measurement.getId() == null) {
            measurementMapper.insert(measurement);
        } else {
            measurementMapper.updateById(measurement);
        }
        feedback.setStatus("已完成");
        feedbackMapper.updateById(feedback);
        return toView(feedback);
    }

    public List<Map<String, Object>> measurements() {
        return feedbackMapper.selectList(Wrappers.<AqiFeedback>lambdaQuery()
                        .eq(AqiFeedback::getStatus, "已完成")
                        .orderByDesc(AqiFeedback::getCreateTime))
                .stream().map(this::toView).collect(Collectors.toList());
    }

    private Map<String, Object> toView(AqiFeedback feedback) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", feedback.getId());
        row.put("feedbackNo", feedback.getFeedbackNo());
        row.put("supervisorId", feedback.getSupervisorId());
        row.put("province", feedback.getProvince());
        row.put("city", feedback.getCity());
        row.put("address", feedback.getAddress());
        row.put("estimate", feedback.getEstimatedAqiLevel());
        row.put("description", feedback.getDescription());
        row.put("status", feedback.getStatus());
        row.put("workerId", feedback.getAssignedWorkerId());
        row.put("assignType", feedback.getAssignType());
        row.put("assignTime", feedback.getAssignTime());
        row.put("createTime", feedback.getCreateTime());
        if (feedback.getAssignedWorkerId() != null) {
            GridWorker worker = workerMapper.selectById(feedback.getAssignedWorkerId());
            row.put("workerName", worker == null ? "-" : worker.getRealName());
        }
        AqiMeasurement measurement = measurementMapper.selectOne(Wrappers.<AqiMeasurement>lambdaQuery()
                .eq(AqiMeasurement::getFeedbackId, feedback.getId()));
        if (measurement != null) {
            row.put("so2", measurement.getSo2Level());
            row.put("co", measurement.getCoLevel());
            row.put("pm25", measurement.getPm25Level());
            row.put("finalAqi", measurement.getFinalAqiLevel());
            row.put("measureTime", measurement.getMeasureTime());
        }
        return row;
    }

    private AqiFeedback requireFeedback(Long id) {
        AqiFeedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            throw new IllegalArgumentException("反馈记录不存在");
        }
        return feedback;
    }

    private boolean sameArea(AqiFeedback feedback, GridWorker worker) {
        return feedback.getProvince().equals(worker.getProvince()) && feedback.getCity().equals(worker.getCity());
    }

    private String createFeedbackNo() {
        String prefix = "NEP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = feedbackMapper.selectCount(Wrappers.<AqiFeedback>lambdaQuery()
                .likeRight(AqiFeedback::getFeedbackNo, prefix));
        return prefix + String.format("%03d", count + 1);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
