package com.example.library_management.controller;

import com.example.library_management.entity.Book;
import com.example.library_management.entity.Notifications;
import com.example.library_management.entity.User;
import com.example.library_management.service.AuthenticationService;
import com.example.library_management.service.BookService;
import com.example.library_management.service.OrderService;
import com.example.library_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lib/user")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final AuthenticationService authenticationService;

    @PostMapping("/addtocart/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToCart(@PathVariable("id") long id) {
        userService.addToCart(id);
    }

    @GetMapping("/mycart")
    @ResponseStatus(HttpStatus.OK)
    public List<Book> getBYUsers() {
        return userService.checkMyCart();
    }

    @PostMapping("/submitorder")
    @ResponseStatus(HttpStatus.CREATED)
    public String submitOrder() {
        orderService.createOrder();
        return "you're order has been submited";
    }

    @DeleteMapping("/delete/book/cart/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBookInCart(@PathVariable("id") Long id) {
        userService.deleteBookFromCart(id);
    }

    @DeleteMapping("/unban/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void unBanUser(@PathVariable("id") Long id) {
        userService.unBanUser(id);
    }

    @GetMapping("/mynotis")
    @ResponseStatus(HttpStatus.OK)
    public List<Notifications> userNotis() {
        return userService.myNotis();
    }


}
