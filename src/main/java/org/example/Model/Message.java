package org.example.Model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_user_id")
    private User recipient;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime created_at;

    @Column(name = "text", nullable = false)
    private String text;

    public Message() {}

    public Message(User sender, User recipient, LocalDateTime created_at, String text){
        this.sender=sender;
        this.recipient=recipient;
        this.created_at=created_at;
        this.text=text;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }

    public void setSender(User sender) { this.sender = sender; }

    public User getRecipient() { return recipient; }

    public void setRecipient(User recipient) { this.recipient = recipient; }

    public LocalDateTime getCreated_at() { return created_at; }

    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    @PrePersist
    public void prePersist() { created_at = LocalDateTime.now(); }
}
