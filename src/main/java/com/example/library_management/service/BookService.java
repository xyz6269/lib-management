package com.example.library_management.service;

import com.example.library_management.DTO.BooksDto;
import com.example.library_management.entity.Book;
import com.example.library_management.entity.User;
import com.example.library_management.repository.BooksRepository;
import com.example.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BooksRepository booksRepository;
    private final UserRepository userRepository;

    public void addBook(BooksDto request) {
        Optional<Book> exists = booksRepository.findByIsbn(request.getIsbn());
        if (exists.isPresent()) {
            log.info("lmaooooooooooooooooooooooooo");
            exists.get().setQuantity(exists.get().getQuantity()+ request.getQuantity());
        }else {
            log.info("tessssssssssssssssssssssssst");
            Book newBook = Book.builder()
                    .isbn(request.getIsbn())
                    .quantity(request.getQuantity())
                    .cover(request.getCover())
                    .build();
            booksRepository.save(newBook);
        }
    }

    public List<Book> allBooks() {
        return booksRepository.findAll();
    }

    public Book getBookbyId(Long id) {
        return booksRepository.findById(id).orElseThrow(()-> new RuntimeException("Book doesn't exist in the inventory"));
    }
    public Book getBookbyIsbn(String isbn) {
        return booksRepository.findByIsbn(isbn).orElseThrow(()-> new RuntimeException("Book doesn't exist in the inventory"));
    }
    public void removeBook(Long bookId) {
        Book booktoRemove = getBookbyId(bookId);
        booktoRemove.getUser().forEach(user -> user.getBooks().remove(booktoRemove));
        booktoRemove.getUser().clear();
        booksRepository.delete(booktoRemove);

    }






}
