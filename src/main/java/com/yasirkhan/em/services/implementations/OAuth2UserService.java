package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.entities.Employee;
import com.yasirkhan.em.entities.User;
import com.yasirkhan.em.entities.enums.Role;
import com.yasirkhan.em.repositories.EmployeeRepository;
import com.yasirkhan.em.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public OAuth2UserService(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = attributes.get("name").toString();
        String email = attributes.get("email").toString();
        String providerId = oAuth2User.getName();

        Boolean isEmailVerified = (Boolean) attributes.getOrDefault("email_verified",false);
        if (registrationId.equals("google") && !isEmailVerified) {
            throw new OAuth2AuthenticationException("Your email is not verified by the google. Please Verify your email first");
        }

        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);

        User user;

        if (optionalEmployee.isEmpty()) {
            // New User Registration
            User newUser = User.builder()
                    .role(Role.EMPLOYEE)
                    .googleId(providerId)
                    .build();

            Employee employee = Employee.builder()
                    .name(name)
                    .email(email)
                    .department("Backend")
                    .salary(1000.0)
                    .joiningDate(LocalDate.now())
                    .user(newUser)
                    .build();

            Employee newEmployee = employeeRepository.save(employee);
            user = newEmployee.getUser();
        } else {
            // Existing User Update
            Employee existingEmployee = optionalEmployee.get();
            user = existingEmployee.getUser();

            // Update Google ID if it wasn't set previously
            if (user.getGoogleId() == null || !user.getGoogleId().equals(providerId)) {
                user.setGoogleId(providerId);
                user = userRepository.save(user);
            }
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "email"
        );
    }
}
