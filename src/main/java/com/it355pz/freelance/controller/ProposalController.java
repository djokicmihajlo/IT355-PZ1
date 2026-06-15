package com.it355pz.freelance.controller;

import com.it355pz.freelance.controller.form.ProposalForm;
import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.service.FileStorageService;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalService;
import com.it355pz.freelance.service.ResourceNotFoundException;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ProposalController {

    private final ProposalService proposalService;
    private final FreelanceJobService freelanceJobService;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    public ProposalController(ProposalService proposalService, FreelanceJobService freelanceJobService,
                              UserService userService, FileStorageService fileStorageService) {
        this.proposalService = proposalService;
        this.freelanceJobService = freelanceJobService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/jobs/{jobId}/proposals")
    public String listJobProposals(@PathVariable Long jobId, Model model) {
        model.addAttribute("pageTitle", "Prijave za posao");
        model.addAttribute("job", freelanceJobService.getById(jobId));
        model.addAttribute("proposals", proposalService.findByJobId(jobId));
        return "proposals/list";
    }

    @GetMapping("/jobs/{jobId}/proposals/new")
    public String newProposalForm(@PathVariable Long jobId, Model model) {
        model.addAttribute("pageTitle", "Nova prijava");
        model.addAttribute("job", freelanceJobService.getById(jobId));
        model.addAttribute("proposalForm", new ProposalForm());
        model.addAttribute("freelancers", findFreelancers());
        return "proposals/form";
    }

    @PostMapping("/jobs/{jobId}/proposals")
    public String createProposal(@PathVariable Long jobId, @ModelAttribute ProposalForm proposalForm,
                                 @RequestParam("cvFile") MultipartFile cvFile, Model model) {
        try {
            proposalService.create(jobId, proposalForm.getFreelancerId(), proposalForm.getProposalText(),
                    proposalForm.getOfferedPrice(), proposalForm.getEstimatedDays(), cvFile);
            return "redirect:/jobs/" + jobId + "/proposals";
        } catch (ValidationException | ResourceNotFoundException ex) {
            model.addAttribute("pageTitle", "Nova prijava");
            model.addAttribute("job", freelanceJobService.getById(jobId));
            model.addAttribute("proposalForm", proposalForm);
            model.addAttribute("freelancers", findFreelancers());
            model.addAttribute("errorMessage", ex.getMessage());
            return "proposals/form";
        }
    }

    @GetMapping("/proposals/{proposalId}/cv")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long proposalId) {
        Proposal proposal = proposalService.getById(proposalId);
        CvAttachment attachment = proposal.getCvAttachment();
        Resource resource = fileStorageService.loadAsResource(attachment);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(attachment.getOriginalFileName())
                        .build()
                        .toString())
                .body(resource);
    }

    private List<User> findFreelancers() {
        return userService.findAll().stream()
                .filter(User::isFreelancer)
                .toList();
    }
}
