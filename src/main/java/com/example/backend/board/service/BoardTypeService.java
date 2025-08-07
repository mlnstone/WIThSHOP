package com.example.backend.board.service;

import com.example.backend.board.dto.BoardTypeRequestDto;
import com.example.backend.board.dto.BoardTypeResponseDto;
import com.example.backend.board.entity.Board;
import com.example.backend.board.entity.BoardType;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.repository.BoardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardTypeService {

    private final BoardRepository boardRepository;
    private final BoardTypeRepository boardTypeRepository;

    // 게시판 타입 전체 조회
    public List<BoardTypeResponseDto> getAllBoardTypes() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();
        return boardTypes.stream()
                .map(BoardTypeResponseDto::from)
                .collect(Collectors.toList());
    }

    // 게시판 타입 생성
    @Transactional
    public BoardTypeResponseDto createBoardType(BoardTypeRequestDto dto) {
        if (boardTypeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 게시판 타입입니다.");
        }
        BoardType boardType = BoardType.builder()
                .name(dto.getName())
                .build();

        boardTypeRepository.save(boardType);
        return BoardTypeResponseDto.from(boardType);
    }

    // 게시판 타입 수정
    @Transactional
    public BoardTypeResponseDto updateBoardType(Long boardTypeId, BoardTypeRequestDto dto) {
        BoardType boardType = boardTypeRepository.findById(boardTypeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판 타입이 존재하지 않습니다."));
        if (!boardType.getName().equals(dto.getName()) &&
                boardTypeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 게시판 타입 이름입니다.");
        }

        boardType.updateName(dto.getName());
        return BoardTypeResponseDto.from(boardType);
    }

    @Transactional
    public void deleteBoardType(Long boardTypeId) {
        BoardType boardType = boardTypeRepository.findById(boardTypeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 타입입니다."));

        List<Board> boards = boardRepository.findAllByBoardType(boardType);

        if (!boards.isEmpty()) {
            List<Long> boardIds = boards.stream()
                    .map(Board::getBoardId)
                    .toList();

            throw new IllegalStateException("해당 게시판 타입을 사용하는 게시글(ID: " + boardIds + ")이 존재하여 삭제할 수 없습니다.");
        }

        boardTypeRepository.delete(boardType);
    }
}