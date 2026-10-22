package com.example.logcollector.repository;

import com.example.logcollector.model.LogEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogEntryRepository extends MongoRepository<LogEntry, ObjectId> {
}
