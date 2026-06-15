package com.it355pz.freelance.controller;

import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.UserService;
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
    public String home(Model model) {
        model.addAttribute("pageTitle", "Freelance poslovi");
        model.addAttribute("jobCount", freelanceJobService.count());
        model.addAttribute("proposalCount", proposalService.count());
        model.addAttribute("userCount", userService.count());
        return "index";
    }
}
