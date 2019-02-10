package ru.geekbrains.gkportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.gkportal.entity.CommunicationType;
import ru.geekbrains.gkportal.exception.CommunicationTypeNotFoundException;
import ru.geekbrains.gkportal.repository.CommunicationTypeRepository;

import java.util.function.Supplier;

@Service
public class CommunicationTypeService {

    public static final String PHONE_DESCRIPTION = "Телефон";
    public static final String EMAIL_DESCRIPTION = "Email";
    public static final String EMAIL_TYPE_GUID = "dd2ff57f-901e-4dee-b914-56659187fe85";
    public static final String PHONE_TYPE_GUID = "b5bc1897-c6f4-4efd-9dbd-cf89002997e4";


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
