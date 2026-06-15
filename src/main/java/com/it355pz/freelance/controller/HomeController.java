package com.it355pz.freelance.controller;

import com.it355pz.freelance.repository.ApplicationData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ApplicationData data;

    public HomeController(ApplicationData data) {
        this.data = data;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Freelance poslovi");
        model.addAttribute("jobCount", data.getJobs().size());
        model.addAttribute("proposalCount", data.getProposals().size());
        model.addAttribute("userCount", data.getUsers().size());
        return "index";
    }
}
