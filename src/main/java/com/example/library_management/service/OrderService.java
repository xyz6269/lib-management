package com.example.library_management.service;

import com.example.library_management.entity.*;
import com.example.library_management.repository.BooksRepository;
import com.example.library_management.repository.NotificationRepository;
import com.example.library_management.repository.OrderRepository;
import com.example.library_management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthenticationService authenticationService;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final BooksRepository booksRepository;

    public void createOrder() {
        log.info("tf now");
        User currentUser = authenticationService.getCurrentUser();
        if (currentUser.getOrder() != null) {
            throw new RuntimeException("you already submitted an ordered that is still being processed");
        }
        log.info(String.valueOf(currentUser));
        log.info("testetestest");
        log.info(currentUser.getBooks().toString());
        if (currentUser.getBooks().isEmpty()) {

            throw new RuntimeException("you're cart is empty");
        }
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderOwner(currentUser.getEmail());
        order.setUser(currentUser);

        Notifications newNoti = Notifications.builder()
                .text("your order has been submited")
                .customDate(LocalDateTime.now())
                .build();
        notificationRepository.save(newNoti);
        log.info(newNoti.toString());
        currentUser.addNoti(newNoti);
        log.info(currentUser.getNotifications().toString());
        userRepository.save(currentUser);
        log.info(order.toString());
        orderRepository.save(order);
        log.info(currentUser.getNotifications().toString());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Book> getOrderDetails(Long id) {
        Order userOrder = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("no order"));
        User targetUser = userOrder.getUser();
        List<Book> userCart = targetUser.getBooks();
        Collections.sort(userCart,Comparator.comparing(Book::getQuantity));

        return userCart;
    }

    public String rejectOrder(Long id) {
        log.info("elon MID");
        Order orderToReject = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("no order"));
        User orderUser = orderToReject.getUser();
        orderUser.getBooks().forEach(book -> book.getUser().remove(orderUser));
        orderUser.getBooks().clear();
        Notifications newNoti = Notifications.builder()
                .text("your order application has been rejected")
                .customDate(LocalDateTime.now())
                .build();
        orderUser.getNotifications().add(newNoti);
        userRepository.save(orderUser);
        orderRepository.delete(orderToReject);
        return "your order has been terminated by the system";
    }

    public String validateOrder(Long id) {
        Order userOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("no order"));
        User orderUser = userOrder.getUser();
        userOrder.setCreatedAt(LocalDateTime.now());
        userOrder.setEndTime(userOrder.getCreatedAt().plusDays(7));
        userOrder.setIsOrderValid(true);
        orderRepository.save(userOrder);
        Notifications newNoti = Notifications.builder()
                .text("your order has been validated you borrow duration is 7 days , the books must be returned before "+ userOrder.getEndTime().toString())
                .customDate(LocalDateTime.now())
                .build();
        notificationRepository.save(newNoti);
        orderUser.getNotifications().add(newNoti);
        userRepository.save(orderUser);

        return "your order has been validated come pick it up before";
    }
    public void refresh() {
        final List<Order> orders = orderRepository.findAll();
        orders.stream().filter(order -> order.getEndTime().compareTo(LocalDateTime.now()) <= 0).map(Order::getId).forEach(this::punishUser);
        orders.forEach(order -> order.getEndTime().equals(LocalDateTime.now()));
    }

    private void punishUser(Long id) {
        Order orderToreject = orderRepository.findById(id).orElseThrow(()-> new RuntimeException("no order"));
        User userToban = orderToreject.getUser();
        rejectOrder(orderToreject.getId());
        userService.banUser(userToban.getId());

    }

    public void validateReturn(Long id) {
        Order returnedOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("no order"));
        User orderUser = returnedOrder.getUser();
        orderUser.getBooks().forEach(book -> book.getUser().remove(orderUser));
        orderUser.getBooks().clear();
        orderRepository.delete(returnedOrder);
    }
    public List<Order> getValidatedOrders() {
        List<Order> allOrders = getAllOrders();
        log.info(allOrders.toString());
        List<Order> validOrders = new ArrayList<>();
        for (Order order : allOrders ) {

            if (order.getIsOrderValid() != null) validOrders.add(order);
        }
        return validOrders;
    }

    public List<Order> getUvnalidatedOrders() {
        List<Order> allOrders = getAllOrders();
        List<Order> nonValidOrders = new ArrayList<>();
        for (Order order : allOrders ) {
            if (order.getIsOrderValid() == null) nonValidOrders.add(order);
        }

        return nonValidOrders;
    }


}