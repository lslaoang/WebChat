package com.messaging.webchat.event;

import com.messaging.webchat.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class WebSocketEventListener {

    public static  final String NEW_WEB_SOCKET_CONNECTION = "Received a new webs socket connection";
    public static  final String USER_DISCONNECTED = "User Disconnected: ";

    private static final Logger logger = LoggerFactory.getLogger((WebSocketEventListener.class));

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void webSocketConnectListener(SessionConnectedEvent sessionConnectedEvent){
        logger.info(NEW_WEB_SOCKET_CONNECTION);
    }

    @EventListener
    public void webSocketDisconnectionListener(SessionDisconnectEvent sessionDisconnectEvent){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username!=null){
            logger.info((USER_DISCONNECTED +username));

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public",chatMessage);
        }
    }
}
