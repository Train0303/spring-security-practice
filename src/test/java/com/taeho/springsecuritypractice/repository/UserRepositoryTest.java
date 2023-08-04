package com.taeho.springsecuritypractice.repository;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception404;
import com.taeho.springsecuritypractice.user.User;
import com.taeho.springsecuritypractice.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("회원 레포지토리 테스트")
@DataJpaTest
public class UserRepositoryTest {

    private final EntityManager em;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, EntityManager em) {
        this.userRepository = userRepository;
        this.em = em;
    }

    @BeforeEach
    public void setUp() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        User user = new User(null,"test", "test@gmail.com", passwordEncoder.encode("test12!"), "ROLE_USER");
        userRepository.save(user);
    }

    @DisplayName("회원 저장 테스트")
    @Test
    public void user_save_test() {
        // given
        User user = newUser("rjsdnxogh12", "rjsdnxogh55@gmail.com");

        // when
        user = userRepository.save(user);

        // then
        assertEquals(2,user.getId());
        assertEquals("rjsdnxogh12",user.getUsername());
        assertEquals("rjsdnxogh55@gmail.com",user.getEmail());
        assertEquals("ROLE_USER",user.getRoles());
    }

    @DisplayName("회원 저장 테스트 실패 : 같은 이메일 존재")
    @Test
    public void user_save_test_fail_already_exist_email() {
        // given
        User user = newUser("rjsdnxogh12", "test@gmail.com");

        // when & then
        Exception e = assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @DisplayName("회원 찾기 : 이메일")
    @Test
    public void user_findByEmail_test() {
        // given
        String email = "test@gmail.com";

        // when
        User user = userRepository.findByEmail(email).get();

        // then
        assertEquals(1L, user.getId());
    }

    @DisplayName("회원 찾기 실패 : 없는 이메일")
    @Test
    public void user_findByEmail_test_fail_not_exist_email() {
        // given
        String email = "test11@gmail.com";

        // when
        Exception e = assertThrows(Exception404.class, () -> userRepository.findByEmail(email).orElseThrow( () -> new Exception404("없는 이메일입니다.")));

        // then
        assertEquals("없는 이메일입니다.", e.getMessage());
    }


    private User newUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode("test12!"))
                .roles("ROLE_USER")
                .build();
    }
}
