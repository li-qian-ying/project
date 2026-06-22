package com.neusoft.nep.controller;
import com.neusoft.nep.dto.FeedbackCreateRequest;
import com.neusoft.nep.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;
    @GetMapping public List<Map<String,Object>> list(@RequestParam(required=false) Long supervisorId, @RequestParam(required=false) Long workerId, @RequestParam(required=false) String status) { return feedbackService.list(supervisorId, workerId, status); }
    @PostMapping public Map<String,Object> create(@RequestBody FeedbackCreateRequest request) { return feedbackService.create(request); }
}
