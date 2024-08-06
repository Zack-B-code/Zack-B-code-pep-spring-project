package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

@RestController

public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public @ResponseBody ResponseEntity<String> postRegisterHandler(@RequestBody String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(body, Account.class);
        Account addedAccount = accountService.UserAdd(account);
        if (addedAccount != null) {
            return ResponseEntity.ok(mapper.writeValueAsString(addedAccount));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public @ResponseBody ResponseEntity<String> postLoginHandler(@RequestBody String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(body, Account.class);
        Account loggedInAccount = accountService.UserLoggedin(account);
        if (loggedInAccount != null) {
            return ResponseEntity.ok(mapper.writeValueAsString(loggedInAccount));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/messages")
    public @ResponseBody ResponseEntity<String> postMessagesHandler(@RequestBody String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(body, Message.class);
        Message createdMessage = messageService.CreateMessage(message);
        if (createdMessage != null) {
            return ResponseEntity.ok(mapper.writeValueAsString(createdMessage));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesHandler() {
        List<Message> messages = messageService.RetrieveAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{message_id}")
    public @ResponseBody ResponseEntity<String> getMessageByIDHandler(@PathVariable("message_id") Integer messageId) {
        try {
            Message message = messageService.RetrieveMessageByMessageId(messageId);
            if (message != null) {
                ObjectMapper mapper = new ObjectMapper();
                return ResponseEntity.ok(mapper.writeValueAsString(message));
            } else {
                return ResponseEntity.ok(""); // Return an empty response body if the message is not found
            }
        } catch (JsonProcessingException e) {
            // Log the exception
            System.err.println("Error processing JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        } catch (ResponseStatusException e) {
            // Log the exception
            System.err.println("Error in retrieve operation: " + e.getReason());
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (Exception e) {
            // Log the exception
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }


    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<String> deleteMessageByIDHandler(@PathVariable("message_id") Integer messageId) {
        try {
            int rowsDeleted = messageService.DeleteMessageByMessageId(messageId);
            if (rowsDeleted == 1) {
                return ResponseEntity.ok("1"); // Return "1" for a successful deletion
            } else {
                return ResponseEntity.ok(""); // Return an empty response body when the message is not found
            }
        } catch (ResponseStatusException e) {
            // Log the exception
            System.err.println("Error in delete operation: " + e.getReason());
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (Exception e) {
            // Log the exception
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }

    @PatchMapping("/messages/{message_id}")
    public @ResponseBody ResponseEntity<String> updateMessageByIDHandler(@PathVariable("message_id") Integer messageId, @RequestBody String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Message message = mapper.readValue(body, Message.class);
            int rowsUpdated = messageService.UpdateMessage(messageId, message);
            if (rowsUpdated > 0) {
                return ResponseEntity.ok(mapper.writeValueAsString(rowsUpdated));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found or validation failed");
            }
        } catch (JsonProcessingException e) {
            // Log the exception
            System.err.println("Error parsing JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON");
        } catch (ResponseStatusException e) {
            // Log the exception
            System.err.println("Error in update operation: " + e.getReason());
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (Exception e) {
            // Log the exception
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }

    @GetMapping("/accounts/{account_id}/messages")
    public @ResponseBody ResponseEntity<String> getAllMessagesByUserHandler(@PathVariable("account_id") Integer accountId) {
        try {
            List<Message> userMessages = messageService.RetrieveAllMessageForUser(accountId);
            ObjectMapper mapper = new ObjectMapper();
            if (userMessages != null) {
                return ResponseEntity.ok(mapper.writeValueAsString(userMessages));
            } else {
                return ResponseEntity.ok(mapper.writeValueAsString(List.of()));
            }
        } catch (JsonProcessingException e) {
            // Log the exception
            System.err.println("Error processing JSON: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        } catch (ResponseStatusException e) {
            // Log the exception
            System.err.println("Error in retrieve operation: " + e.getReason());
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        } catch (Exception e) {
            // Log the exception
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }

}
