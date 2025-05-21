package org.example.Service;
import org.example.DTO.MessageDTO;
import org.example.DTO.UserDTO;
import org.example.Mapper.MessageMapper;
import org.example.Model.Message;
import org.example.Model.User;
import org.example.Repository.MessageRepository;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private NotificationService notificationService;

    public void sendMessage(UserDTO senderDTO, UserDTO recipientDTO, String content) {
        User sender = userRepository.findByEmail(senderDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Отправитель не найден"));

        User recipient = userRepository.findByEmail(recipientDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Получатель не найден"));
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setText(content);

        messageRepository.save(message);
        notificationService.createAndSendNotification(recipient.getId(), "Вы получили новое сообщение.", null);
    }

    public List<MessageDTO> getMessages(UserDTO senderDTO, UserDTO recipientDTO) {
        User sender = userRepository.findByEmail(senderDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Отправитель не найден"));

        User recipient = userRepository.findByEmail(recipientDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Получатель не найден"));

        List<Message> messages = messageRepository.findMessagesBySenderAndRecipient(sender, recipient);
        return messages.stream()
                .map(messageMapper::toDTO)
                .collect(Collectors.toList());
    }
}