package com.neusoft.nep.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.neusoft.nep.entity.*;
import com.neusoft.nep.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {
    private final PublicSupervisorMapper supervisorMapper;
    private final GridWorkerMapper workerMapper;
    private final GridAreaMapper areaMapper;
    private final AqiFeedbackMapper feedbackMapper;
    private final AqiMeasurementMapper measurementMapper;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[][] WORKERS = {
            {"grid001","张网格员","辽宁省","沈阳市","可工作"},{"grid002","李网格员","辽宁省","大连市","可工作"},
            {"grid003","陈网格员","广东省","深圳市","可工作"},{"grid004","赵网格员","广东省","广州市","支援"},
            {"grid005","刘网格员","四川省","成都市","可工作"},{"grid006","周网格员","上海市","上海市","可工作"},
            {"grid007","王网格员","北京市","北京市","可工作"},{"grid008","孙网格员","四川省","绵阳市","可工作"},
            {"grid009","冯网格员","河北省","石家庄市","可工作"},{"grid010","钱网格员","山东省","济南市","可工作"},
            {"grid011","吴网格员","浙江省","杭州市","可工作"},{"grid012","郑网格员","湖北省","武汉市","可工作"},
            {"grid013","何网格员","江苏省","南京市","可工作"},{"grid014","高网格员","陕西省","西安市","支援"}
    };
    private static final String[][] AREAS = {
            {"辽宁省","沈阳市"},{"辽宁省","大连市"},{"辽宁省","鞍山市"},{"辽宁省","抚顺市"},
            {"北京市","北京市"},{"上海市","上海市"},{"广东省","广州市"},{"广东省","深圳市"},{"广东省","珠海市"},
            {"四川省","成都市"},{"四川省","绵阳市"},{"河北省","石家庄市"},{"河北省","唐山市"},
            {"山东省","济南市"},{"山东省","青岛市"},{"浙江省","杭州市"},{"浙江省","宁波市"},
            {"湖北省","武汉市"},{"江苏省","南京市"},{"江苏省","苏州市"},{"陕西省","西安市"}
    };
    // 编号、省份、城市、地址、预估、说明、状态、网格员编号、指派类型、创建时间
    private static final String[][] FEEDBACKS = {
            {"NEP20260616001","辽宁省","沈阳市","浑南区高新路2号","轻度污染","能见度一般，有轻微异味。","已指派","grid001","本地指派","2026-06-16 09:40:00"},
            {"NEP20260615002","辽宁省","大连市","中山区人民路附近","良","空气状况较好。","已完成","grid002","本地指派","2026-06-15 09:25:00"},
            {"NEP20260614003","广东省","深圳市","南山区科技园北区","中度污染","有扬尘，午后气味明显。","待指派","","","2026-06-14 15:10:00"},
            {"NEP20260613004","北京市","北京市","朝阳区建国路88号","轻度污染","早高峰期间能见度下降。","已完成","grid007","本地指派","2026-06-13 08:35:00"},
            {"NEP20260612005","上海市","上海市","浦东新区世纪大道","重度污染","道路施工，扬尘明显。","已完成","grid006","本地指派","2026-06-12 09:10:00"},
            {"NEP20260611006","四川省","成都市","高新区天府大道","轻度污染","午后有轻微灰霾。","已完成","grid005","本地指派","2026-06-11 13:45:00"},
            {"NEP20260610007","广东省","广州市","天河区体育西路","良","空气状况正常，申请例行核验。","已完成","grid004","本地指派","2026-06-10 09:35:00"},
            {"NEP20260609008","辽宁省","沈阳市","铁西区建设大路","中度污染","工厂周边存在异味。","已完成","grid001","本地指派","2026-06-09 14:55:00"},
            {"NEP20260608009","四川省","绵阳市","涪城区临园路","良","天气晴朗，能见度良好。","已完成","grid008","本地指派","2026-06-08 08:40:00"},
            {"NEP20260607010","广东省","深圳市","宝安区机场南路","轻度污染","车流量较大，有尾气气味。","已指派","grid003","本地指派","2026-06-07 12:10:00"},
            {"NEP20260606011","辽宁省","鞍山市","铁东区胜利南路","中度污染","空气中颗粒物较明显。","待指派","","","2026-06-06 16:30:00"},
            {"NEP20260605012","广东省","珠海市","香洲区情侣中路","优","海风较大，空气清新。","待指派","","","2026-06-05 10:20:00"},
            {"NEP20260604013","河北省","石家庄市","长安区中山东路","中度污染","午间灰霾明显，能见度下降。","已完成","grid009","本地指派","2026-06-04 10:25:00"},
            {"NEP20260603014","山东省","济南市","历下区经十路","轻度污染","道路车流密集，有尾气气味。","已完成","grid010","本地指派","2026-06-03 09:50:00"},
            {"NEP20260602015","浙江省","杭州市","西湖区文三路","良","申请例行空气质量核验。","已完成","grid011","本地指派","2026-06-02 09:15:00"},
            {"NEP20260601016","湖北省","武汉市","洪山区珞喻路","重度污染","施工区域扬尘持续时间较长。","已完成","grid012","本地指派","2026-06-01 13:00:00"},
            {"NEP20260531017","江苏省","南京市","建邺区江东中路","轻度污染","晚高峰空气有轻微异味。","已完成","grid013","本地指派","2026-05-31 17:35:00"},
            {"NEP20260530018","陕西省","西安市","雁塔区长安南路","中度污染","天气静稳，颗粒物聚集。","已完成","grid014","本地指派","2026-05-30 14:45:00"},
            {"NEP20260529019","浙江省","宁波市","鄞州区首南中路","良","空气状况总体良好。","待指派","","","2026-05-29 11:20:00"},
            {"NEP20260528020","山东省","青岛市","市南区香港中路","轻度污染","港区方向有轻微异味。","已指派","grid010","异地指派","2026-05-28 13:55:00"},
            {"NEP20260527021","河北省","唐山市","路北区建设路","中度污染","工业区域附近可见烟尘。","待指派","","","2026-05-27 15:05:00"},
            {"NEP20260526022","江苏省","苏州市","工业园区星湖街","良","园区空气状况例行检测。","已完成","grid013","异地指派","2026-05-26 10:10:00"},
            {"NEP20260525023","辽宁省","沈阳市","和平区青年大街","良","交通主干道例行核验。","已完成","grid001","本地指派","2026-05-25 09:00:00"},
            {"NEP20260524024","辽宁省","沈阳市","大东区滂江街","轻度污染","车辆尾气气味较明显。","已指派","grid001","本地指派","2026-05-24 10:40:00"},
            {"NEP20260523025","辽宁省","沈阳市","沈北新区蒲河路","优","空气清新，申请对照检测。","已完成","grid001","本地指派","2026-05-23 08:20:00"},
            {"NEP20260522026","辽宁省","沈阳市","于洪区黄海路","中度污染","附近施工产生持续扬尘。","已指派","grid001","本地指派","2026-05-22 15:50:00"},
            {"NEP20260521027","辽宁省","沈阳市","苏家屯区枫杨路","良","居民区空气质量例行反馈。","已完成","grid001","本地指派","2026-05-21 09:45:00"},
            {"NEP20260520028","辽宁省","沈阳市","浑南区智慧大街","轻度污染","早间能见度略有下降。","已指派","grid001","本地指派","2026-05-20 08:55:00"},
            {"NEP20260519029","辽宁省","沈阳市","铁西区北二路","中度污染","工业厂区周边有异味。","已完成","grid001","本地指派","2026-05-19 14:05:00"},
            {"NEP20260518030","辽宁省","沈阳市","皇姑区黄河北大街","轻度污染","晚间道路扬尘较多。","已指派","grid001","本地指派","2026-05-18 17:35:00"}
    };
    // 反馈编号、SO2、CO、PM2.5、最终AQI
    private static final String[][] MEASUREMENTS = {
            {"NEP20260615002","良","优","良","良"},{"NEP20260613004","良","轻度污染","良","轻度污染"},
            {"NEP20260612005","中度污染","良","重度污染","重度污染"},{"NEP20260611006","良","良","轻度污染","轻度污染"},
            {"NEP20260610007","优","良","良","良"},{"NEP20260609008","中度污染","良","轻度污染","中度污染"},
            {"NEP20260608009","优","优","良","良"},{"NEP20260604013","轻度污染","良","中度污染","中度污染"},
            {"NEP20260603014","良","轻度污染","良","轻度污染"},{"NEP20260602015","优","良","良","良"},
            {"NEP20260601016","中度污染","良","重度污染","重度污染"},{"NEP20260531017","良","轻度污染","轻度污染","轻度污染"},
            {"NEP20260530018","中度污染","良","中度污染","中度污染"},{"NEP20260526022","优","良","良","良"},
            {"NEP20260525023","优","良","良","良"},{"NEP20260523025","优","优","优","优"},
            {"NEP20260521027","良","优","良","良"},{"NEP20260519029","中度污染","良","轻度污染","中度污染"}
    };

    @Override public void run(ApplicationArguments args) {
        Long supervisorId = ensureSupervisor();
        for (String[] row : WORKERS) ensureWorker(row);
        for (String[] row : AREAS) ensureArea(row);
        for (String[] row : FEEDBACKS) ensureFeedback(row, supervisorId);
        for (String[] row : MEASUREMENTS) ensureMeasurement(row);
    }

    private Long ensureSupervisor() {
        PublicSupervisor supervisor = supervisorMapper.selectOne(Wrappers.<PublicSupervisor>lambdaQuery().eq(PublicSupervisor::getPhone, "13924689016"));
        if (supervisor == null) { supervisor = new PublicSupervisor(); supervisor.setPhone("13924689016"); supervisor.setPassword("123456"); supervisor.setRealName("林子涵"); supervisor.setAge(22); supervisor.setGender("女"); supervisorMapper.insert(supervisor); }
        return supervisor.getId();
    }
    private GridWorker worker(String code) { return workerMapper.selectOne(Wrappers.<GridWorker>lambdaQuery().eq(GridWorker::getWorkerCode, code)); }
    private void ensureWorker(String[] row) {
        if (worker(row[0]) != null) return;
        GridWorker item = new GridWorker(); item.setWorkerCode(row[0]); item.setPassword("123456"); item.setRealName(row[1]); item.setProvince(row[2]); item.setCity(row[3]); item.setWorkStatus(row[4]); workerMapper.insert(item);
    }
    private void ensureArea(String[] row) {
        if (areaMapper.selectCount(Wrappers.<GridArea>lambdaQuery().eq(GridArea::getProvince,row[0]).eq(GridArea::getCity,row[1])) > 0) return;
        GridArea item = new GridArea(); item.setProvince(row[0]); item.setCity(row[1]); item.setEnabled(true); areaMapper.insert(item);
    }
    private void ensureFeedback(String[] row, Long supervisorId) {
        if (feedbackMapper.selectCount(Wrappers.<AqiFeedback>lambdaQuery().eq(AqiFeedback::getFeedbackNo,row[0])) > 0) return;
        AqiFeedback item = new AqiFeedback(); item.setFeedbackNo(row[0]); item.setSupervisorId(supervisorId); item.setProvince(row[1]); item.setCity(row[2]); item.setAddress(row[3]); item.setEstimatedAqiLevel(row[4]); item.setDescription(row[5]); item.setStatus(row[6]); item.setCreateTime(LocalDateTime.parse(row[9],TIME));
        if (!row[7].isEmpty()) { GridWorker assigned = worker(row[7]); item.setAssignedWorkerId(assigned.getId()); item.setAssignType(row[8]); item.setAssignTime(item.getCreateTime().plusMinutes(30)); }
        feedbackMapper.insert(item);
    }
    private void ensureMeasurement(String[] row) {
        AqiFeedback feedback = feedbackMapper.selectOne(Wrappers.<AqiFeedback>lambdaQuery().eq(AqiFeedback::getFeedbackNo,row[0]));
        if (feedback == null || feedback.getAssignedWorkerId() == null || measurementMapper.selectCount(Wrappers.<AqiMeasurement>lambdaQuery().eq(AqiMeasurement::getFeedbackId,feedback.getId())) > 0) return;
        AqiMeasurement item = new AqiMeasurement(); item.setFeedbackId(feedback.getId()); item.setWorkerId(feedback.getAssignedWorkerId()); item.setFeedbackNo(row[0]); item.setSo2Level(row[1]); item.setCoLevel(row[2]); item.setPm25Level(row[3]); item.setFinalAqiLevel(row[4]); item.setMeasureTime(feedback.getCreateTime().plusHours(2)); measurementMapper.insert(item);
    }
}
