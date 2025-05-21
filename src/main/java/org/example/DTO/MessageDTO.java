package org.example.DTO;
import org.example.Model.User;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long message_id;
    private User sender;
    private User recipient;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private String text;

    public MessageDTO() {}

    public Long getMessage_id() { return message_id; }

    public void setMessage_id(Long message_id) { this.message_id = message_id; }

    public User getSender() { return sender; }

    public void setSender(User sender) { this.sender = sender; }

    public User getRecipient() { return recipient; }

    public void setRecipient(User recipient) { this.recipient = recipient; }

    public LocalDateTime getCreated_at() { return created_at; }

    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getUpdated_at() { return updated_at; }

    public void setUpdated_at(LocalDateTime updated_at) { this.updated_at = updated_at; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }
}
