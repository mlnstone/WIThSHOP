package com.example.backend.board.service;

import com.example.backend.board.dto.BoardRequestDto;
import com.example.backend.board.dto.BoardResponseDto;
import com.example.backend.board.entity.Board;
import com.example.backend.board.entity.BoardType;
import com.example.backend.board.repository.BoardRepository;
import com.example.backend.board.repository.BoardTypeRepository;
import com.example.backend.user.entity.User;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardTypeRepository boardTypeRepository;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    public BoardResponseDto getBoard(Long boardId, String ip, String userEmail) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        String redisKey;
        if (userEmail != null) {
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            redisKey = "board:view:" + boardId + ":" + user.getUserId();
        } else {
            redisKey = "board:view:" + boardId + ":" + ip;
        }

        boolean isFirstView = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofHours(24));
        if (isFirstView) {
            board.increaseHit();
        }

        return BoardResponseDto.from(board);
    }

    public Page<BoardResponseDto> getAllBoard(Pageable pageable, String search, Long typeId) {
        Page<Board> boards;

        boolean hasSearch = StringUtils.hasText(search);

        if (typeId != null) {
            boards = hasSearch
                    ? boardRepository.findByBoardType_BoardTypeIdAndBoardTitleContaining(typeId, search, pageable)
                    : boardRepository.findByBoardType_BoardTypeId(typeId, pageable);
        } else {
            boards = hasSearch
                    ? boardRepository.findByBoardTitleContaining(search, pageable)
                    : boardRepository.findAll(pageable);
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