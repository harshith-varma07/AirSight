package com.air.airquality.controller;
import com.air.airquality.model.User;
import com.air.airquality.repository.UserRepository;
import com.air.airquality.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginData) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));

        String token = jwtUtil.generateToken(loginData.getUsername());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}