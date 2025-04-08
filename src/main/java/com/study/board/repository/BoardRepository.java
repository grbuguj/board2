package com.study.board.repository;

import com.study.board.entity.Board;
import com.study.board.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // ✅ 특정 회원이 작성한 모든 게시글 조회
    List<Board> findAllByMember(MemberEntity member);
}
