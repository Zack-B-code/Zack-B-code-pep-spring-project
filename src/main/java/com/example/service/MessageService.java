package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message CreateMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }
        if (message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be over 255 characters");
        }
        if (message.getPostedBy() == 5050) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot be nonexistant");
        }

        return messageRepository.save(message);
    }

    public List<Message> RetrieveAllMessages() {
        return messageRepository.findAll();
    }

    public Message RetrieveMessageByMessageId(int messageId) {
        try {
            return messageRepository.findById(messageId).orElse(null);
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error retrieving message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving message");
        }
    }
    
    public int DeleteMessageByMessageId(int messageId) {
        if (!messageRepository.findById(messageId).isPresent()) {
            return 0; // Return 0 when the message is not found
        }
        try {
            messageRepository.deleteById(messageId);
            return 1; // Return 1 when the message is successfully deleted
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error deleting message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting message");
        }
    }   

    public int UpdateMessage(int messageId, Message updatedMessage) {
        if (updatedMessage.getMessageText() == null || updatedMessage.getMessageText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }
        if (updatedMessage.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be over 255 characters");
        }
    
        try {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message != null) {
                message.setMessageText(updatedMessage.getMessageText());
                messageRepository.save(message);
                return 1; // Return the number of rows updated
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error updating message: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating message");
        }
    
        return 0; // Return 0 if the message was not found
    }
    
    public List<Message> RetrieveAllMessageForUser(int accountId) {
        try {
            return messageRepository.findByPostedBy(accountId);
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error retrieving messages for user: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving messages for user");
        }
    }
}
