package com.neusoft.nep.controller;
import com.neusoft.nep.dto.*;
import com.neusoft.nep.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api")
public class ManagementController {
    private final FeedbackService feedbackService; private final SystemDataService systemDataService;
    @PostMapping("/assignments") public Map<String,Object> assign(@RequestBody AssignRequest request) { return feedbackService.assign(request); }
    @PostMapping("/measurements") public Map<String,Object> measure(@RequestBody MeasurementRequest request) { return feedbackService.measure(request); }
    @GetMapping("/measurements") public List<Map<String,Object>> measurements() { return feedbackService.measurements(); }
    @GetMapping("/workers") public List<Map<String,Object>> workers() { return systemDataService.workers(); }
    @GetMapping("/areas") public List<Map<String,Object>> areas() { return systemDataService.areas(); }
    @GetMapping("/stats") public Map<String,Object> stats() { return systemDataService.stats(); }
}
