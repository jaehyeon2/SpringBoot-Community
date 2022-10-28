package com.example.SpringBootCommunity;

import com.example.SpringBootCommunity.domain.Board;
import com.example.SpringBootCommunity.domain.User;
import com.example.SpringBootCommunity.domain.enums.BoardType;
import com.example.SpringBootCommunity.repository.BoardRepository;
import com.example.SpringBootCommunity.repository.UserRepository;
import com.example.SpringBootCommunity.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class BootWebApplication extends WebMvcConfigurerAdapter {
	public static void main(String[] args){
		SpringApplication.run(BootWebApplication.class, args);
	}

	@Autowired
	private UserArgumentResolver userArgumentResolver;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers){
		argumentResolvers.add(userArgumentResolver);
	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepository,
									BoardRepository boardRepository) throws Exception{
		return (args)->{
			User user = userRepository.save(
					User.builder()
							.name("jaehyeon")
							.password("test")
							.email("test@gmail.com")
							.createdDate(LocalDateTime.now())
							.build()
			);

			IntStream.rangeClosed(1, 200).forEach(index->
					boardRepository.save(Board.builder()
							.title("게시글" + index)
							.subTitle("순서" + index)
							.content("콘텐츠")
							.boardType(BoardType.free)
							.createdDate(LocalDateTime.now())
							.updatedDate(LocalDateTime.now())
							.user(user)
							.build())
			);
		};
	}
}
