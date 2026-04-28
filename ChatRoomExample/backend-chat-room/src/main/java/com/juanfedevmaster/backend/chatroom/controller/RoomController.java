package com.juanfedevmaster.backend.chatroom.controller;

import com.juanfedevmaster.backend.chatroom.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final ParticipantService participantService;

    /**
     * Lista todos los participantes activos en una sala.
     * GET /api/rooms/{roomId}/participants
     */
    @GetMapping("/{roomId}/participants")
    public ResponseEntity<Set<String>> getParticipants(@PathVariable String roomId) {
        return ResponseEntity.ok(participantService.getParticipants(roomId));
    }
}
