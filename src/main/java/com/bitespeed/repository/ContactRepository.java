package com.bitespeed.repository;

import com.bitespeed.daos.Contact;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(value = "select * from contact c1 where c1.email=?1 union select * from contact c2 where c2.phone_number=?2 " , nativeQuery = true)
    List<Contact> findContactsByEmail(String email, String phonenumber);

    @Transactional
    @Modifying
    @Query(value = "update contact set linked_precedence = 1 , linked_id = ?1 where id = ?2 ", nativeQuery = true)
    void updateLinkedPrecedenceLikedID(Long primaryIndexId , Long secondaryIndexId);

}
