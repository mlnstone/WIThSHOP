package com.example.backend.board.controller;

import com.example.backend.board.dto.BoardTypeRequestDto;
import com.example.backend.board.dto.BoardTypeResponseDto;
import com.example.backend.board.service.BoardTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판타입설정", description = "게시판타입설정")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class BoardTypeController {
    private final BoardTypeService boardTypeService;

    @Operation(summary = "게시판 타입 전체 조회")
    @GetMapping("/admin/board-types")
    public List<BoardTypeResponseDto> getAllBoardTypes() {
        return boardTypeService.getAllBoardTypes();
    }

    @Operation(summary = "게시판 타입 등록")
    @PostMapping("/admin/board-types")
    public ResponseEntity<BoardTypeResponseDto> createBoardType(
            @RequestBody BoardTypeRequestDto boardTypeRequestDto
    ) {
        BoardTypeResponseDto boardTypeResponseDto = boardTypeService.createBoardType(boardTypeRequestDto);
        return ResponseEntity.ok(boardTypeResponseDto);
    }

    @Operation(summary = "게시판 타입 수정")
    @PutMapping("/admin/board-types/{boardTypeId}")
    public ResponseEntity<BoardTypeResponseDto> updateBoardType(
            @PathVariable Long boardTypeId,
            @RequestBody BoardTypeRequestDto boardTypeRequestDto
    ) {
        BoardTypeResponseDto boardTypeResponseDto = boardTypeService.updateBoardType(boardTypeId, boardTypeRequestDto);
        return ResponseEntity.ok(boardTypeResponseDto);
    }

    @Operation(summary = "게시판 타입 삭제")
    @DeleteMapping("/admin/board-types/{boardTypeId}")
    public ResponseEntity<String> deleteBoardType(
            @PathVariable Long boardTypeId
    ) {
        boardTypeService.deleteBoardType(boardTypeId);
        return ResponseEntity.status(HttpStatus.OK).body("게시판 타입이 삭제되었습니다.");
    }
}
