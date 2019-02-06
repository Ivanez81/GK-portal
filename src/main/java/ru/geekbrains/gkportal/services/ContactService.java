package ru.geekbrains.gkportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.gkportal.DTO.FlatRegDTO;
import ru.geekbrains.gkportal.entities.Contact;
import ru.geekbrains.gkportal.entities.Flat;
import ru.geekbrains.gkportal.entities.SystemAccount;
import ru.geekbrains.gkportal.repository.ContactRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContactService {

    private ContactRepository contactRepository;
    private FlatsService flatsService;
    private CommunicationService communicationService;

    @Autowired
    public void setContactRepository(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    public void setFlatsService(FlatsService flatsService) {
        this.flatsService = flatsService;
    }

    @Autowired
    public void setCommunicationService(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public Contact createContact(SystemAccount systemAccount) throws Throwable {
        List<Flat> flatArray= new ArrayList<>();
        for (FlatRegDTO flatDTO: systemAccount.getFlats()
             ) {
            flatArray.add(flatsService.createFlat(flatDTO));
        }
        Contact contact = Contact.builder()
                .contactType(systemAccount.getContactType())
                .firstName(systemAccount.getFirstName())
                .lastName(systemAccount.getLastName())
                .middleName(systemAccount.getMiddleName())
                .flats(flatArray)
                .build();

        contact.setCommunications(communicationService.createCommunication(systemAccount, contact));

        return contact;


    }

}

