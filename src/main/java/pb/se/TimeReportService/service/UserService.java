package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pb.se.TimeReportService.annotation.UserNotFoundException;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.port.persistence.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        return userRepository.findById(username).orElseThrow(UserNotFoundException::new);
    }

    public User getUser(User user) {
        return userRepository.findById(user.getUsername()).orElseThrow(UserNotFoundException::new);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public boolean deleteUser(String username) {
        return userRepository.findById(username)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

}
