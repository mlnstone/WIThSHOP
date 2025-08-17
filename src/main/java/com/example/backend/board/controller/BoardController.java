package com.example.backend.board.controller;

import com.example.backend.board.dto.BoardRequestDto;
import com.example.backend.board.dto.BoardResponseDto;
import com.example.backend.board.service.BoardService;
import com.example.backend.common.base.EntityDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "게시판", description = "게시판")
@RestController
@RequiredArgsConstructor
public class BoardController extends EntityDate {

    private final BoardService boardService;

    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/board/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(
            @PathVariable Long boardId,
            Principal principal,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        String userEmail = principal != null ? principal.getName() : null;

        BoardResponseDto boardResponseDto = boardService.getBoard(boardId, ip, userEmail);
        return ResponseEntity.status(HttpStatus.OK).body(boardResponseDto);
    }

    @Operation(summary = "게시글 전체 조회")
    @GetMapping("/board")
    public ResponseEntity<Page<BoardResponseDto>> getBoardList(
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long typeId   // ← 추가
    ) {
        Page<BoardResponseDto> dto = boardService.getAllBoard(pageable, search, typeId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "게시글 작성")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/board")
    public ResponseEntity<BoardResponseDto> createBoard(
            @RequestBody BoardRequestDto boardRequestDto
    ) {
        BoardResponseDto boardResponseDto = boardService.createBoard(boardRequestDto);
        return ResponseEntity.ok(boardResponseDto);
    }

    @Operation(summary = "게시글 수정")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/board/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardRequestDto boardRequestDto
    ) {
        BoardResponseDto boardResponseDto = boardService.updateBoard(boardId, boardRequestDto);
        return ResponseEntity.ok(boardResponseDto);
    }

    @Operation(summary = "게시글 삭제")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long boardId
    ) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.status(HttpStatus.OK).body("게시글이 삭제되었습니다.");
    }

}
