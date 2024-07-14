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

    @DBRef
    private Task task;

    private int hoursWorked;


    public WorkLog(UUID id, User user, LocalDate date, Task task, int hoursWorked) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.task = task;
        this.hoursWorked = hoursWorked;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Task getTask() {
        return task;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public User getUser() {
        return user;
    }

    public WorkLog assignUser(User user) {
        return new WorkLog(this.id, user, this.date, this.task, this.hoursWorked);
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
