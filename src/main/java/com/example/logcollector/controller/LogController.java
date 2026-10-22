package com.example.logcollector.controller;

import com.example.logcollector.model.LogEntry;
import com.example.logcollector.service.LogService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<LogEntry> create(@RequestBody LogEntry logEntry) {
        LogEntry savedLog = logService.save(logEntry);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedLog.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedLog);
    }

    @GetMapping
    public Page<LogEntry> findAll(@PageableDefault(size = 50) Pageable pageable) {
        return logService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> findById(@PathVariable ObjectId id) {
        return logService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
