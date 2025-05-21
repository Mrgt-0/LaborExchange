package org.example.Controller;
import org.example.Config.MyUserDetails;
import org.example.DTO.MessageDTO;
import org.example.DTO.UserDTO;
import org.example.Service.MessageService;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @GetMapping("/chat/{recipientEmail}")
    public String showMessages(@PathVariable String recipientEmail, Model model, Authentication authentication) {
        logger.info("Открытие формы чата.");
        logger.info("Поиск пользователя с email: {}", recipientEmail);
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        UserDTO currentUser = myUserDetails.getUser();
        UserDTO recipient = userService.findByEmail(recipientEmail);

        if (recipient == null) {
            model.addAttribute("error", "Пользователь не найден.");
            logger.error("Пользователь '{}' не найден.", recipientEmail);
            return "error"; // обработка ошибки
        }

        List<MessageDTO> messages = messageService.getMessages(currentUser, recipient);
        model.addAttribute("messages", messages);
        model.addAttribute("recipient", recipient);
        logger.info("Пользователь '{}' найден. Получение сообщений.", recipient.getEmail());
        return "chat"; // ваша страница чата
    }

    // Метод для отправки сообщения
    @PostMapping("/send-message")
    public String sendMessage(@RequestParam("recipient") String recipientEmail,
                              @RequestParam("content") String content, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        UserDTO currentUserDTO = myUserDetails.getUser();

        UserDTO recipientDTO = userService.findByEmail(recipientEmail);
        if (recipientDTO == null) {
            logger.error("Не удалось отправить сообщение. Получатель '{}' не найден.", recipientEmail);
            return "error"; // обработка ошибки
        }

        messageService.sendMessage(currentUserDTO, recipientDTO, content);
        logger.info("Сообщение '{}' успешно отправлено от '{}' к '{}'.", content, currentUserDTO.getEmail(), recipientEmail);
        return "redirect:/chat/" + recipientEmail; // редирект на чат с получателем
    }

    @GetMapping("/chat/messages/{recipientEmail}")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable String recipientEmail, Authentication authentication) {
        logger.info("Поиск пользователя с email: {}", recipientEmail);
        if (authentication.getPrincipal() instanceof MyUserDetails myUserDetails) {
            UserDTO currentUser = myUserDetails.getUser();
            UserDTO recipientUser = userService.findByEmail(recipientEmail);
            if (recipientUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<MessageDTO> messages = messageService.getMessages(currentUser, recipientUser);
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}