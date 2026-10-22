package com.example.logcollector.service;

import com.example.logcollector.model.LogEntry;
import com.example.logcollector.repository.LogEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogService {

    private final LogEntryRepository repository;
    private final MongoTemplate mongoTemplate;

    public LogService(LogEntryRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public synchronized LogEntry save(LogEntry logEntry) {
        if (logEntry.getCorrelationId() == null || logEntry.getCorrelationId().isBlank()) {
            throw new IllegalArgumentException("correlationId is required");
        }

        Query query = Query.query(Criteria.where("correlationId").is(logEntry.getCorrelationId()));
        Update update = appendEntries(logEntry)
                .setOnInsert("correlationId", logEntry.getCorrelationId());
        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(true).returnNew(true);

        try {
            return mongoTemplate.findAndModify(query, update, options, LogEntry.class);
        } catch (DuplicateKeyException exception) {
            LogEntry updatedLog = mongoTemplate.findAndModify(
                    query, appendEntries(logEntry), FindAndModifyOptions.options().returnNew(true), LogEntry.class);
            if (updatedLog == null) {
                throw exception;
            }
            return updatedLog;
        }
    }

    public Page<LogEntry> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<LogEntry> findById(ObjectId id) {
        return repository.findById(id);
    }

    private Update appendEntries(LogEntry logEntry) {
        Update update = new Update();
        append(update, "requests", logEntry.getRequests());
        append(update, "responses", logEntry.getResponses());
        return update;
    }

    private void append(Update update, String field, List<?> entries) {
        if (entries != null && !entries.isEmpty()) {
            update.push(field).each(entries.toArray());
        }
    }
}
