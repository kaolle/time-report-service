package pb.se.TimeReportService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "worklogs")
@CompoundIndex(def = "{'user': 1, 'date': 1}")
public class WorkLog {

    @Id
    private UUID id;
    @DBRef
    @Indexed
    private final User user;

    private LocalDate date;

    private UUID taskId;

    private int hoursWorked;


    public WorkLog(UUID id, User user, LocalDate date, UUID taskId, int hoursWorked) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.taskId = taskId;
        this.hoursWorked = hoursWorked;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public User getUser() {
        return user;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public WorkLog assignUser(User user) {
        return new WorkLog(this.id, user, this.date, this.taskId, this.hoursWorked);
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
