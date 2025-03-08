// package com.example.IotProject.controller;

// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/api/v1/chat")
// public class ChatController {
// private final SimpMessagingTemplate messagingTemplate;

// public ChatController(SimpMessagingTemplate messagingTemplate) {
// this.messagingTemplate = messagingTemplate;
// }

// @PostMapping("/send")
// public String sendMessage(@RequestBody String message) {
// // Gửi tin nhắn đến tất cả client đang subscribe "/topic/messages"
// messagingTemplate.convertAndSend("/topic/messages", message);
// return "Message sent: " + message;
// }

// @GetMapping("/test")
// public String test() {
// return "Test";
// }
// }
