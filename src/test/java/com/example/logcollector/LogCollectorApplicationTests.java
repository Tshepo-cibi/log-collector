package com.example.logcollector;

import com.example.logcollector.model.LogEntry;
import com.example.logcollector.repository.LogEntryRepository;
import com.example.logcollector.service.LogService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "spring.data.mongodb.uri=mongodb://localhost:27017/log-collector-test",
        "spring.data.mongodb.auto-index-creation=false"
})
class LogCollectorApplicationTests {

    @Autowired
    private LogEntryRepository repository;

    @Autowired
    private LogService logService;

    @Test
    void contextLoads() {
    }

    @Test
    void appendsRequestsAndResponsesForExistingCorrelationId() {
        String correlationId = UUID.randomUUID().toString();
        LogEntry firstLog = createLog(correlationId, "Tshepo", "0000");
        LogEntry secondLog = createLog(correlationId, "Another", "0001");

        LogEntry savedLog = logService.save(firstLog);
        String id = savedLog.getId();
        assertNotNull(id);
        try {
            LogEntry updatedLog = logService.save(secondLog);
            assertEquals(id, updatedLog.getId());

            LogEntry retrievedLog = repository.findById(new ObjectId(id)).orElseThrow();
            assertEquals(correlationId, retrievedLog.getCorrelationId());
            assertEquals(2, retrievedLog.getRequests().size());
            assertEquals(2, retrievedLog.getResponses().size());
            assertEquals("Tshepo", retrievedLog.getRequests().get(0).getRequest().get("firstname"));
            assertEquals("Another", retrievedLog.getRequests().get(1).getRequest().get("firstname"));
        } finally {
            repository.deleteById(new ObjectId(id));
        }
    }

    private LogEntry createLog(String correlationId, String firstname, String responseCode) {
        LogEntry.ServiceRequest request = new LogEntry.ServiceRequest();
        request.setServiceName("proposal");
        request.setRequest(Map.of("firstname", firstname));

        LogEntry.ServiceResponse response = new LogEntry.ServiceResponse();
        response.setServiceName("proposal");
        response.setResponse(Map.of("code", responseCode));

        LogEntry logEntry = new LogEntry();
        logEntry.setCorrelationId(correlationId);
        logEntry.setRequests(List.of(request));
        logEntry.setResponses(List.of(response));
        return logEntry;
    }
}
