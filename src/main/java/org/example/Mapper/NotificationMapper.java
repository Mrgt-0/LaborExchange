package org.example.Mapper;

import org.example.DTO.NotificationDTO;
import org.example.Model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toEntity(NotificationDTO notificationDTO);
    NotificationDTO toDTO(Notification notification);
}
