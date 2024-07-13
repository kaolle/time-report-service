package pb.se.TimeReportService.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Document(collection = "customers")
public class Customer {

    @Id
    private UUID id;
    private String name;
    @DBRef
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
}
