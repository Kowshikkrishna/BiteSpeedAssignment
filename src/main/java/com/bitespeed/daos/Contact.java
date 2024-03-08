package com.bitespeed.daos;

import com.bitespeed.enums.LinkedPrecedence;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;


@Entity
@Table(name = "contact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;
    private String email;
    private Integer linkedId;
    private LinkedPrecedence linkedPrecedence;
    @CreationTimestamp
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
