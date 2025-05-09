package org.example.DTO;

import org.example.Enum.ApplicationStatus;

public class ApplicationViewDTO {
    private Long id;
    private String status;
    private String vacancyTitle;

    public ApplicationViewDTO(long id, ApplicationStatus status, String title) {
        this.id = id;
        this.status = status.name();
        this.vacancyTitle = title;
    }
}