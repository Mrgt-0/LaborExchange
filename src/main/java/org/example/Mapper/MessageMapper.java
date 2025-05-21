package org.example.Mapper;
import org.example.DTO.MessageDTO;
import org.example.Model.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message toEntity(MessageDTO messageDTO);
    MessageDTO toDTO(Message message);
}
