package com.example.library_management.service;


import com.example.library_management.entity.*;
import com.example.library_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final NotificationRepository notificationRepository;
    private final OrderRepository orderRepository;
    private final BooksRepository booksRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService;
    private final BookService bookService;
    private final BanList banList;


    public List<User> getallUsers() {
        log.info("ayyyyyyyyyyyyyyyyyyyyyyyyo wtf");
        return userRepository.findAll();
    }

    public String isAdmin() {
        return "admin logged in";
    }

    public void deleteUser(long id) {
        User userToDelte = userRepository.findById(id).orElseThrow(()-> new RuntimeException("user doesn't exist"));
        Order userOrder = userToDelte.getOrder();
        userToDelte.getBooks().forEach(book -> book.getUser().remove(userToDelte));
        log.info(userToDelte.getBooks().toString());
        userToDelte.getBooks().clear();
        if (userToDelte.getOrder() != null) {
            orderRepository.delete(userOrder);
        }
        userRepository.delete(userToDelte);
    }

    public List<Notifications> myNotis() {
        User user = authenticationService.getCurrentUser();
        log.info(user.getNotifications().toString());
        return user.getNotifications();
    }

    public User getUserById(long id)  {
        return userRepository.
                findById(id).
                orElseThrow(
                        ()-> new RuntimeException("user not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.
                findByEmail(email).
                orElseThrow(
                        ()-> new RuntimeException("user not found"));
    }


    public void addToCart(long id) {
        User currentUser = authenticationService.getCurrentUser();
        log.info(currentUser.getBooks().toString()+ "shiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiit");
        if (currentUser.getOrder() != null) throw new RuntimeException("you can't modify your cart while your order is submitted and being processed");
        log.info(currentUser.getBooks().toString());
        log.info(currentUser.getEmail()+"  hhhhhhhhhhhhhhhhhhhhhhh");
        Book bookToAdd = booksRepository.findById(id)
                .orElseThrow(
                        ()-> new RuntimeException
                                ("this book isn't available")
                );

        log.info(currentUser.getBooks().toString().toString() + currentUser.getEmail());
        if(currentUser.getBooks().stream().anyMatch(book -> book==bookToAdd)) {
             throw new RuntimeException("you can't have 2 copies of the same book");
        }
        if (bookToAdd.getQuantity() == 0) throw new RuntimeException("this book is outta stock");

        if (currentUser.getBooks().stream().count() >= 4) {
            log.info("noooooooooooooooooooooooooooooooooooooooooOOOOOOOOO");
            throw new RuntimeException("cart at full capacity");
        }
        log.info("DMC=<MGRR");
        currentUser.addBooktoCart(bookToAdd);

        userRepository.save(currentUser);
        booksRepository.save(bookToAdd);

        log.info(currentUser.getBooks().toString()+" ayoooooooooooooooooooo wtf");
    }

    public List<Book> getUserCart(Long id) {
        List<Integer> orderingList = new ArrayList<>();
        List<Book> orderDetails = new ArrayList<>();
        User target = userRepository.findById(id)
                .orElseThrow(
                        ()-> new RuntimeException("user doesn't exist")
                );
        
        return target.getBooks();
    }


    public List<Book> checkMyCart() {
        User currentUser = authenticationService.getCurrentUser();
        return currentUser.getBooks();
    }

    public void deleteBookFromCart(Long id) {
        User currentUser= authenticationService.getCurrentUser();
        Book bookToRemove = bookService.getBookbyId(id);
        if (currentUser.getOrder() == null) {
            bookToRemove.getUser().remove(currentUser);
            currentUser.getBooks().remove(bookToRemove);
        }else {
            throw new RuntimeException("you can't modify your cart while your order is submitted and being processed");
        }
    }

    public void banUser(Long id) {
        String bannedUserEmail = getUserById(id).getEmail();
        deleteUser(id);
        BannedUser bannedUser = new BannedUser();
        bannedUser.setUserEmail(bannedUserEmail);
        banList.save(bannedUser);
    }

    public void unBanUser(Long id) {
        BannedUser userToUnBan = banList.findById(id).orElseThrow(() -> new RuntimeException("no such user exist in the ban list"));
        banList.delete(userToUnBan);

    }




}
