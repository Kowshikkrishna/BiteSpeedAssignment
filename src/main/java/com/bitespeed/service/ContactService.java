package com.bitespeed.service;

import com.bitespeed.daos.Contact;
import com.bitespeed.dtos.ContactResponse;
import com.bitespeed.dtos.Request;
import com.bitespeed.dtos.Response;
import com.bitespeed.enums.LinkedPrecedence;
import com.bitespeed.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ContactService {
    @Autowired
    private final ContactRepository contactRepository;
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Response getContacts(Request request) {
        Response response = new Response();
        List<Contact> contactList =  contactRepository
                .findContactsByEmail(request.getEmail() , request.getPhoneNumber());
        List<Contact> primaryContactList = contactList.stream()
                .filter(contact -> contact.getLinkedPrecedence().equals(LinkedPrecedence.PRIMARY))
                .toList();
        switch (primaryContactList.size()) {
            case 0 -> {
                if (contactList.size() == 0)
                    response.setContactResponse(createContact(request));
                else
                    response.setContactResponse(
                            convertContactToContactResponse(contactList, contactList.get(0).getLinkedId())
                    );
            }
            case 1 -> response.setContactResponse(
                    createContact(request, primaryContactList.get(0).getId().intValue(), contactList)
            );
            case 2 -> response.setContactResponse(convertSecondaryToPrimary(contactList));
        }
        return response;
    }

    ContactResponse createContact(Request request) {
        Contact contact = Contact.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .linkedId(null)
                .linkedPrecedence(LinkedPrecedence.PRIMARY)
                .build();
        Contact contactDao = contactRepository.save(contact);
        return convertContactToContactResponse(contactDao);
    }

    ContactResponse createContact(Request request, Integer primaryContactId ,List<Contact> contactList) {
        Contact contact = Contact.builder()
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .linkedId(primaryContactId)
                .linkedPrecedence(LinkedPrecedence.SECONDARY)
                .build();
        Contact contactDao = contactRepository.save(contact);
        contactList.add(contactDao);
        return convertContactToContactResponse(contactList, primaryContactId);
    }

    ContactResponse convertContactToContactResponse(Contact contact) {
        return ContactResponse.builder()
                .primaryContactId(contact.getId().intValue())
                .emails(List.of(contact.getEmail()))
                .phoneNumbers(List.of(contact.getPhoneNumber()))
                .secondaryContactIds(null)
                .build();
    }

    ContactResponse convertContactToContactResponse(List<Contact> contactList , Integer primaryContactId) {
        return ContactResponse.builder()
                .primaryContactId(primaryContactId)
                .emails(contactList.stream().map(Contact::getEmail).filter(Objects::nonNull).distinct().toList())
                .phoneNumbers(contactList.stream().map(Contact::getPhoneNumber).filter(Objects::nonNull).distinct().toList())
                .secondaryContactIds(contactList.stream()
                        .filter(contact -> contact.getLinkedPrecedence().equals(LinkedPrecedence.SECONDARY))
                        .map(contact -> contact.getId().intValue()).
                        toList())
                .build();
    }

    ContactResponse convertSecondaryToPrimary(List<Contact> contactList) {
        Integer secondaryContactIndex =
                contactList.get(0)
                        .getCreatedAt()
                        .before(contactList.get(0).getCreatedAt()) ? 0 : 1;
        contactRepository.updateLinkedPrecedence(contactList.get(secondaryContactIndex).getId());
        contactList.get(secondaryContactIndex).setLinkedPrecedence(LinkedPrecedence.SECONDARY);
        return convertContactToContactResponse(
                contactList , contactList.get(secondaryContactIndex ^ 1).getId().intValue()
        );
    }

}
