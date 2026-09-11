package com.example.demo.user;

import com.example.demo.user.dto.RegisterUserRequest;
import com.example.demo.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String EMAIL = "ivan@gmail.com";
    private static final String JMBG = "1122334455667";
    private static final String RAW_PASSWORD = "ivan1234";
    private static final String HASHED_PASSWORD = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    @DisplayName("successful registration saves hashed password")
    void registerSavesHashed() {

        var request = new RegisterUserRequest(JMBG, "Ivan", "Ivanović", EMAIL, RAW_PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepository.existsByJmbg(JMBG)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getPasswordHashed()).isEqualTo(HASHED_PASSWORD);
        assertThat(saved.getPasswordHashed()).isNotEqualTo(RAW_PASSWORD);
        verify(passwordEncoder).encode(RAW_PASSWORD);

    }

    @Test
    @DisplayName("registration response does not contain JMBG and hashed password")
    void registerResponseHidesSensitiveData() {
        var request = new RegisterUserRequest(JMBG, "Ivan", "Ivanović", EMAIL, RAW_PASSWORD);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(userRepository.existsByJmbg(JMBG)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto result = userService.register(request);

        assertThat(result.toString())
                .doesNotContain(RAW_PASSWORD)
                .doesNotContain(HASHED_PASSWORD)
                .doesNotContain(JMBG);
    }
}