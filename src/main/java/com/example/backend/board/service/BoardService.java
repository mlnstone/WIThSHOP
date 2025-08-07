package com.example.backend.board.service;

import com.example.backend.board.dto.BoardRequestDto;
import com.example.backend.board.dto.BoardResponseDto;
import com.example.backend.board.entity.Board;
import com.example.backend.board.entity.BoardType;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.repository.BoardTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardTypeRepository boardTypeRepository;

    public BoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        return BoardResponseDto.from(board);
    }

    public Page<BoardResponseDto> getAllBoard(Pageable pageable, String search) {
        Page<Board> boards;

        if (search != null && !search.isBlank()) {
            boards = boardRepository.findByBoardTitleContaining(search, pageable);
        } else {
            boards = boardRepository.findAll(pageable);
        }

        return boards.map(BoardResponseDto::from);
    }

    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto dto) {
        BoardType boardType = boardTypeRepository.findById(dto.getBoardTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 타입입니다."));

        Board board = Board.builder()
                .boardTitle(dto.getBoardTitle())
                .boardContent(dto.getBoardContent())
                .boardType(boardType)
                .build();

        boardRepository.save(board);
        return BoardResponseDto.from(board);
    }

    @Transactional
    public BoardResponseDto updateBoard(Long boardId, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        BoardType boardType = boardTypeRepository.findById(boardRequestDto.getBoardTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판 타입입니다."));

        board.updateBoard(boardRequestDto, boardType);

        return BoardResponseDto.from(board);
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        boardRepository.deleteById(boardId);
    }
}