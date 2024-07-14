package pb.se.TimeReportService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@SuppressWarnings("unused")
@Document(collection = "whiteListedUser")
public class WhiteListedUser {
    @Id
    String username;

    public WhiteListedUser() {
    }

    public WhiteListedUser(String username) {
        this.username = username;
    }
}
