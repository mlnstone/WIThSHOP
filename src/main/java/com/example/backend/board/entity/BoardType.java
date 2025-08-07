package com.example.backend.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "board_type")
public class BoardType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardTypeId;

    @Column(length = 50)
    private String name;

    public void updateName(String name) {
        this.name = name;
    }
}