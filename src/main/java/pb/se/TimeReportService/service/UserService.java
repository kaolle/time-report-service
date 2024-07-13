package pb.se.TimeReportService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
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
