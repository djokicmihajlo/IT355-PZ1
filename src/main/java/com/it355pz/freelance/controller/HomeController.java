package com.it355pz.freelance.controller;

import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FreelanceJobService freelanceJobService;
    private final ProposalService proposalService;
    private final UserService userService;

    public HomeController(FreelanceJobService freelanceJobService, ProposalService proposalService,
                          UserService userService) {
        this.freelanceJobService = freelanceJobService;
        this.proposalService = proposalService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "Freelancer Platforma");
        model.addAttribute("jobCount", freelanceJobService.count());
        model.addAttribute("proposalCount", proposalService.count());
        model.addAttribute("userCount", userService.count());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Object user = session.getAttribute(SessionKeys.CURRENT_USER);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageTitle", "Kontrolna tabla");
        model.addAttribute("jobCount", freelanceJobService.count());
        model.addAttribute("proposalCount", proposalService.count());
        model.addAttribute("userCount", userService.count());
        return "dashboard";
    }
}
