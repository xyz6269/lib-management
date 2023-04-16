package com.example.library_management.repository;

import com.example.library_management.entity.Book;
import com.example.library_management.entity.User;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {

    public Optional<Book> findByIsbn(String isbn);
    List<Book> findAllByUser(User user);
    void deleteAllByUser(User user);

}
