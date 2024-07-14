package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pb.se.TimeReportService.domain.WorkLog;

import java.util.UUID;
@SuppressWarnings("unused")
public class CreateWorkLogResponse {
    @JsonProperty
    private final UUID id;

    public CreateWorkLogResponse(WorkLog workLog) {
        this.id = workLog.getId();
    }
}
