package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.entity.MemberEntity;
import com.study.board.repository.BoardRepository;
import com.study.board.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 글 작성 처리
    public void write(Board board, String loginEmail) {
        // 로그인된 사용자 정보 가져오기
        MemberEntity member = memberRepository.findByMemberEmail(loginEmail)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        // 게시글에 작성자 정보 설정
        board.setMember(member);

        // 저장
        boardRepository.save(board);
    }



    // 게시글 리스트 처리
    public List<Board> boardList() {
        return boardRepository.findAll();
    }

    // 특정 게시글 불러오기
    public Board boardview(Integer id){

        return boardRepository.findById(id).get();
    }

    // 특정 게시글 삭제
    public void boardDelete(Integer id) {

        boardRepository.deleteById(id);
    }


}
