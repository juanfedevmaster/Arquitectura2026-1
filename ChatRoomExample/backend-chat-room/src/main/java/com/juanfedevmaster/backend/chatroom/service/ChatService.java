package com.juanfedevmaster.backend.chatroom.service;

import com.juanfedevmaster.backend.chatroom.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    public ChatMessage processMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return message;
    }

    public ChatMessage buildJoinMessage(String sender, String roomId) {
        return ChatMessage.builder()
                .type(ChatMessage.MessageType.JOIN)
                .sender(sender)
                .roomId(roomId)
                .content(sender + " joined the room")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ChatMessage buildLeaveMessage(String sender, String roomId) {
        return ChatMessage.builder()
                .type(ChatMessage.MessageType.LEAVE)
                .sender(sender)
                .roomId(roomId)
                .content(sender + " left the room")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ChatMessage buildTypingMessage(String sender, String roomId, boolean isTyping) {
        ChatMessage.MessageType type = isTyping
                ? ChatMessage.MessageType.TYPING
                : ChatMessage.MessageType.STOP_TYPING;
        String content = isTyping ? sender + " is typing..." : "";
        return ChatMessage.builder()
                .type(type)
                .sender(sender)
                .roomId(roomId)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
