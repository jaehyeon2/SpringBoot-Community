package com.example.SpringBootCommunity.repository;

import com.example.SpringBootCommunity.domain.Board;
import com.example.SpringBootCommunity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByUser(User user);
}
