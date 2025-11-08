package com.intellidesk.tool.controller;

import com.intellidesk.tool.dto.*;
import com.intellidesk.tool.service.ApiToolService;
import com.intellidesk.tool.service.NotificationToolService;
import com.intellidesk.tool.service.SqlToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tools")
@RequiredArgsConstructor
public class ToolController {

    private final SqlToolService sqlToolService;
    private final NotificationToolService notificationToolService;
    private final ApiToolService apiToolService;

    @PostMapping("/sql")
    public ResponseEntity<SqlToolResponse> executeSql(@RequestBody SqlToolRequest request) {
        return ResponseEntity.ok(sqlToolService.executeQuery(request));
    }

    @PostMapping("/notification")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationToolRequest request) {
        notificationToolService.sendNotification(request);
        return ResponseEntity.ok("Notification sent successfully");
    }

    @PostMapping("/api")
    public ResponseEntity<ApiToolResponse> invokeApi(@RequestBody ApiToolRequest request) {
        return ResponseEntity.ok(apiToolService.invokeApi(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tool Service is running");
    }
}
