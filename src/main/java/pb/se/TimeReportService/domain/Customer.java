package pb.se.TimeReportService.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;
    private List<Task> tasks;

    public Customer() {
        // Empty constructor needed for mongodb integration.
    }
    @JsonCreator
    public Customer(@JsonProperty("name") String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.tasks = Collections.emptyList();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }
}
