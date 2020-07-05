package edu.mit.cci.pogs.model.dao.export;

import java.util.List;

public interface ExportDao {

    List<EventLogExport> getEventLogExportInfo(List<Long> sessionIds);

    List<CompletedTaskScoreExport> getCompletedTaskScoreExportInfo(List<Long> sessionIds);

    List<EventLogCheckingSummary> getEventLogCheckIn(List<Long> sessionIds);
}
