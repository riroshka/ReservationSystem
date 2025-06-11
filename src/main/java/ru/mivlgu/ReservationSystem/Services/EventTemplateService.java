package ru.mivlgu.ReservationSystem.Services;

import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.EventTemplate;
import ru.mivlgu.ReservationSystem.Repositories.EventTemplateRepository;

@Service
public class EventTemplateService {
    private final EventTemplateRepository repository;

    public EventTemplateService(EventTemplateRepository repository) {
        this.repository = repository;
    }

    public EventTemplate createTemplate(String name, String description, Long duration, byte[] photo) {
        EventTemplate template = new EventTemplate();
        template.setName(name);
        template.setDescription(description);
        template.setDefaultDuration(duration);

        if (photo != null && photo.length > 0) {
            template.setPhoto(photo);
        }

        return repository.save(template);
    }
}