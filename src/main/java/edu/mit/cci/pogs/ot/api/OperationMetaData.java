package edu.mit.cci.pogs.ot.api;

import java.time.Instant;
import java.util.Map;

public class OperationMetaData {

    private long authorId;
    private Instant timestamp;

    private Map<String, String> additionalMetaData;

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getAdditionalMetaData() {
        return additionalMetaData;
    }

    public void setAdditionalMetaData(Map<String, String> additionalMetaData) {
        this.additionalMetaData = additionalMetaData;
    }
}
