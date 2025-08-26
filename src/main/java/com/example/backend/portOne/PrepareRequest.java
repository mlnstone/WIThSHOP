package com.example.backend.portOne;

import java.util.List;

public record PrepareRequest(List<Item> items) {
    public record Item(Long menuId, Long quantity) {
    }
}