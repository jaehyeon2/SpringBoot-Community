package com.example.SpringBootCommunity;

import com.example.SpringBootCommunity.domain.Board;
import com.example.SpringBootCommunity.domain.enums.BoardType;
import com.example.SpringBootCommunity.domain.User;
import com.example.SpringBootCommunity.repository.BoardRepository;
import com.example.SpringBootCommunity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {"spring.config,location=classpath:application-test.yml"})
public class JpaMappingTest {
    private final String boardTestTitle = "테스트";
    private final String email = "test@gmail.com";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    public void init(){
        User user = userRepository.save(
                User.builder()
                .name("test")
                .password("test")
                .email(email)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build());

        boardRepository.save(Board.builder()
                .title(boardTestTitle)
                .subTitle("서브 타이틀")
                .content("콘텐츠")
                .boardType(BoardType.free)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(user)
                .build());
    }

    @Test
    public void 생성_확인_테스트(){
        User user = userRepository.findByEmail(email);
        assertThat(user.getName(), is("test"));
        assertThat(user.getPassword(), is("test"));
        assertThat(user.getEmail(), is(email));

        Board board = boardRepository.findByUser(user);
        assertThat(board.getTitle(), is(boardTestTitle));
        assertThat(board.getSubTitle(), is("서브 타이틀"));
        assertThat(board.getContent(), is("콘텐츠"));
        assertThat(board.getBoardType(), is(BoardType.free));
    }
}