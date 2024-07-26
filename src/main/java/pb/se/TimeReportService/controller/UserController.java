package pb.se.TimeReportService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.GetUserResponse;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.service.UserService;

@RestController
@RequestMapping("/time-report/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping()
    public ResponseEntity<GetUserResponse> getUserAndAllCustomers(@CurrentUser User user) {
        return ResponseEntity.ok(new GetUserResponse(userService.getUser(user)));

    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        //TODO validate that user can only sign-off themselves
        if (userService.deleteUser(username)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
