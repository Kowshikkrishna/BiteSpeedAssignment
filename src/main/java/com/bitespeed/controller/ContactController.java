package com.bitespeed.controller;

import com.bitespeed.dtos.Request;
import com.bitespeed.dtos.Response;
import com.bitespeed.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @Autowired
    private final ContactService service;

    public ContactController(ContactService service) {
        this.service = service;
    }

    @PostMapping("/identity")
    private ResponseEntity<Response> getContacts(@RequestBody Request request)
    {
        Response response = service.getContacts(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
