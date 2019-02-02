package ru.geekbrains.gkportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.gkportal.entities.CommunicationType;
import ru.geekbrains.gkportal.exception.CommunicationTypeNotFoundException;
import ru.geekbrains.gkportal.repository.CommunicationTypeRepository;

import java.util.function.Supplier;

@Service
public class CommunicationTypeService {

    public static final String PHONE_DESCRIPTION = "phone";
    public static final String EMAIL_DESCRIPTION = "email";

    private CommunicationTypeRepository communicationTypeRepository;

    @Autowired
    public void setCommunicationTypeRepository(CommunicationTypeRepository communicationTypeRepository) {
        this.communicationTypeRepository = communicationTypeRepository;
    }

    public CommunicationType findByDescription(String description) throws Throwable {

        return communicationTypeRepository.findByDescription(description).orElseThrow((Supplier<Throwable>) () ->
                new CommunicationTypeNotFoundException(description));
    }

    public CommunicationType findPhoneType() throws Throwable {
        return findByDescription(PHONE_DESCRIPTION);
    }

    public CommunicationType findEmailType() throws Throwable {
        return findByDescription(EMAIL_DESCRIPTION);
    }
}
