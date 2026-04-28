package com.juanfedevmaster.backend.chatroom.controller;

import com.juanfedevmaster.backend.chatroom.model.ChatMessage;
import com.juanfedevmaster.backend.chatroom.service.ChatService;
import com.juanfedevmaster.backend.chatroom.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ParticipantService participantService;

    /**
     * Recibe un mensaje de chat y lo difunde a todos los suscriptores de la sala.
     * Destino cliente: /app/chat/{roomId}/send
     * Broadcast a:    /topic/chat/{roomId}
     */
    @MessageMapping("/chat/{roomId}/send")
    public void sendMessage(@DestinationVariable String roomId,
                            @Payload ChatMessage message) {
        message.setRoomId(roomId);
        ChatMessage processed = chatService.processMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, processed);
    }

    /**
     * Notifica a la sala cuando un usuario se une.
     * Destino cliente: /app/chat/{roomId}/join
     */
    @MessageMapping("/chat/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId,
                         @Payload ChatMessage message,
                         SimpMessageHeaderAccessor headerAccessor) {
        var attrs = headerAccessor.getSessionAttributes();
        if (attrs != null) {
            attrs.put("username", message.getSender());
            attrs.put("roomId", roomId);
        }
        participantService.addParticipant(roomId, message.getSender());
        ChatMessage joinMessage = chatService.buildJoinMessage(message.getSender(), roomId);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, joinMessage);
    }

    /**
     * Notifica a la sala cuando un usuario abandona explícitamente.
     * Destino cliente: /app/chat/{roomId}/leave
     */
    @MessageMapping("/chat/{roomId}/leave")
    public void leaveRoom(@DestinationVariable String roomId,
                          @Payload ChatMessage message) {
        participantService.removeParticipant(roomId, message.getSender());
        ChatMessage leaveMessage = chatService.buildLeaveMessage(message.getSender(), roomId);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, leaveMessage);
    }

    /**
     * Difunde el indicador de escritura a los demás participantes de la sala.
     * El campo 'type' del payload debe ser TYPING o STOP_TYPING.
     * Destino cliente: /app/chat/{roomId}/typing
     * Broadcast a:    /topic/chat/{roomId}/typing
     */
    @MessageMapping("/chat/{roomId}/typing")
    public void typingIndicator(@DestinationVariable String roomId,
                                @Payload ChatMessage message) {
        boolean isTyping = ChatMessage.MessageType.TYPING.equals(message.getType());
        ChatMessage typingMessage = chatService.buildTypingMessage(message.getSender(), roomId, isTyping);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/typing", typingMessage);
    }
}
