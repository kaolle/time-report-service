package pb.se.TimeReportService.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pb.se.TimeReportService.annotation.CurrentUser;
import pb.se.TimeReportService.controller.dto.JwtResponse;
import pb.se.TimeReportService.controller.dto.SigninRequest;
import pb.se.TimeReportService.controller.dto.SignupRequest;
import pb.se.TimeReportService.domain.User;
import pb.se.TimeReportService.port.persistence.UserRepository;
import pb.se.TimeReportService.port.persistence.WhiteListedUserRepository;
import pb.se.TimeReportService.security.CustomUserDetails;
import pb.se.TimeReportService.security.JwtUtils;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    WhiteListedUserRepository whiteListedUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest loginRequest) {

        return authenticate(HttpStatus.OK, loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {


        if (userRepository.findById(signupRequest.getUsername()).isPresent()){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (whiteListedUserRepository.findById(signupRequest.getUsername()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userRepository.save(new User(signupRequest.getUsername(), encoder.encode(signupRequest.getPassword())));

        return authenticate(HttpStatus.CREATED, signupRequest.getUsername(), signupRequest.getPassword());
    }

    @DeleteMapping("/signdown")
    public ResponseEntity selfSignDown(@CurrentUser User user) {

        userRepository.delete(user);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<JwtResponse> authenticate(HttpStatus httpStatus, String requestUsername, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestUsername, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new JwtResponse(jwt,
                "TODO add Id to user",
                userDetails.getUsername(),
                roles), httpStatus);
    }

}
