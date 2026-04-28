package com.juanfedevmaster.backend.chatroom.config;

import com.juanfedevmaster.backend.chatroom.model.ChatMessage;
import com.juanfedevmaster.backend.chatroom.service.ChatService;
import com.juanfedevmaster.backend.chatroom.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ParticipantService participantService;

    /**
     * Maneja desconexiones abruptas del WebSocket y notifica a la sala correspondiente.
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        var attrs = accessor.getSessionAttributes();

        if (attrs == null) return;

        String username = (String) attrs.get("username");
        String roomId   = (String) attrs.get("roomId");

        if (username != null && roomId != null) {
            log.info("User '{}' disconnected from room '{}'", username, roomId);
            participantService.removeParticipant(roomId, username);
            ChatMessage leaveMessage = chatService.buildLeaveMessage(username, roomId);
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, leaveMessage);
        }
    }
}
