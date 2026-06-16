package com.it355pz.freelance.controller;

import com.it355pz.freelance.controller.form.LoginForm;
import com.it355pz.freelance.controller.form.RegisterForm;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("pageTitle", "Prijava");
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("demoAccounts", demoAccounts());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm loginForm, HttpSession session, Model model) {
        try {
            User user = userService.authenticate(loginForm.getUsername(), loginForm.getPassword());
            session.setAttribute(SessionKeys.CURRENT_USER, user);
            return "redirect:/dashboard";
        } catch (ValidationException ex) {
            model.addAttribute("pageTitle", "Prijava");
            model.addAttribute("loginForm", loginForm);
            model.addAttribute("demoAccounts", demoAccounts());
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("pageTitle", "Registracija");
        model.addAttribute("registerForm", new RegisterForm());
        model.addAttribute("roles", UserRole.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterForm registerForm, HttpSession session, Model model) {
        try {
            User user = userService.register(
                    registerForm.getUsername(),
                    registerForm.getPassword(),
                    registerForm.getFullName(),
                    registerForm.getEmail(),
                    registerForm.getRole(),
                    registerForm.getProfileSummary()
            );
            session.setAttribute(SessionKeys.CURRENT_USER, user);
            return "redirect:/dashboard";
        } catch (ValidationException ex) {
            model.addAttribute("pageTitle", "Registracija");
            model.addAttribute("registerForm", registerForm);
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private List<String> demoAccounts() {
        return List.of(
                "client_milan / client123",
                "client_jelena / client123",
                "freelancer_ana / freelancer123",
                "freelancer_marko / freelancer123"
        );
    }
}
