package com.bitespeed.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    private String email;
    private String phoneNumber;
}
