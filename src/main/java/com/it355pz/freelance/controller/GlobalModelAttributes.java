package com.it355pz.freelance.controller;

import com.it355pz.freelance.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("appName")
    public String appName() {
        return "Freelancer Platforma";
    }

    @ModelAttribute("currentUser")
    public User currentUser(HttpSession session) {
        Object user = session.getAttribute(SessionKeys.CURRENT_USER);
        return user instanceof User currentUser ? currentUser : null;
    }
}
