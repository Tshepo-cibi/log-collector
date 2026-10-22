package com.example.logcollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.Map;

@Document(collection = "logs")
public class LogEntry {

    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String correlationId;
    private List<ServiceRequest> requests;
    private List<ServiceResponse> responses;

    public LogEntry() {
    }

    @JsonProperty("_id")
    public String getId() {
        return id == null ? null : id.toString();
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public List<ServiceRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<ServiceRequest> requests) {
        this.requests = requests;
    }

    public List<ServiceResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<ServiceResponse> responses) {
        this.responses = responses;
    }

    public static class ServiceRequest {

        private String serviceName;
        private Map<String, Object> request;

        public ServiceRequest() {
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public Map<String, Object> getRequest() {
            return request;
        }

        public void setRequest(Map<String, Object> request) {
            this.request = request;
        }
    }

    public static class ServiceResponse {

        private String serviceName;
        private Map<String, Object> response;

        public ServiceResponse() {
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public Map<String, Object> getResponse() {
            return response;
        }

        public void setResponse(Map<String, Object> response) {
            this.response = response;
        }
    }
}
