package com.juanfedevmaster.backend.chatroom.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParticipantService {

    // roomId -> conjunto de usernames activos (thread-safe)
    private final ConcurrentHashMap<String, Set<String>> roomParticipants = new ConcurrentHashMap<>();

    public void addParticipant(String roomId, String username) {
        roomParticipants
                .computeIfAbsent(roomId, id -> ConcurrentHashMap.newKeySet())
                .add(username);
    }

    public void removeParticipant(String roomId, String username) {
        Set<String> participants = roomParticipants.get(roomId);
        if (participants != null) {
            participants.remove(username);
            if (participants.isEmpty()) {
                roomParticipants.remove(roomId);
            }
        }
    }

    public Set<String> getParticipants(String roomId) {
        return Collections.unmodifiableSet(
                roomParticipants.getOrDefault(roomId, ConcurrentHashMap.newKeySet())
        );
    }
}
