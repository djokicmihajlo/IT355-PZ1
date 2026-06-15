package com.it355pz.freelance.config;

import com.it355pz.freelance.model.Category;
import com.it355pz.freelance.model.CvAttachment;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.Proposal;
import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.model.UserRole;
import com.it355pz.freelance.repository.ApplicationData;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeedDataInitializer {

    private final ApplicationData data;

    public SeedDataInitializer(ApplicationData data) {
        this.data = data;
    }

    @PostConstruct
    public void seed() {
        if (!data.getUsers().isEmpty()) {
            return;
        }

        Category webDevelopment = new Category(data.nextCategoryId(), "Web razvoj",
                "Izrada i odrzavanje web aplikacija.");
        Category design = new Category(data.nextCategoryId(), "Dizajn",
                "UI/UX dizajn i vizuelni identitet.");
        data.getCategories().addAll(List.of(webDevelopment, design));

        Skill springBoot = new Skill(data.nextSkillId(), "Spring Boot");
        Skill thymeleaf = new Skill(data.nextSkillId(), "Thymeleaf");
        Skill css = new Skill(data.nextSkillId(), "CSS");
        Skill uiDesign = new Skill(data.nextSkillId(), "UI dizajn");
        data.getSkills().addAll(List.of(springBoot, thymeleaf, css, uiDesign));

        User client = new User(data.nextUserId(), "client_milan", "Milan Petrovic",
                "milan@example.com", UserRole.CLIENT, "Vlasnik malog biznisa koji objavljuje freelance poslove.");
        User freelancer = new User(data.nextUserId(), "freelancer_ana", "Ana Markovic",
                "ana@example.com", UserRole.FREELANCER, "Java freelancer sa iskustvom u Spring MVC aplikacijama.");
        data.getUsers().addAll(List.of(client, freelancer));

        FreelanceJob landingPage = new FreelanceJob(data.nextJobId(), "Izrada Spring MVC landing stranice",
                "Potrebna je jednostavna Spring MVC aplikacija sa Thymeleaf prikazima i urednim CSS stilom.",
                new BigDecimal("450.00"), webDevelopment, List.of(springBoot, thymeleaf, css), client,
                LocalDateTime.now().minusDays(2));
        FreelanceJob dashboardDesign = new FreelanceJob(data.nextJobId(), "Redizajn dashboard interfejsa",
                "Potrebno je urediti pregledan dashboard za pracenje prijava i statusa poslova.",
                new BigDecimal("300.00"), design, List.of(css, uiDesign), client,
                LocalDateTime.now().minusDays(1));
        data.getJobs().addAll(List.of(landingPage, dashboardDesign));

        CvAttachment sampleCv = new CvAttachment(data.nextCvAttachmentId(), "ana-markovic-cv.pdf",
                "application/pdf", 245_760, "uploads/cv/sample-ana-markovic-cv.pdf");
        data.getCvAttachments().add(sampleCv);

        Proposal proposal = new Proposal(data.nextProposalId(), landingPage, freelancer,
                "Mogu da implementiram MVC tok sa jasnim service slojem i Thymeleaf prikazima.",
                new BigDecimal("420.00"), 5, sampleCv, LocalDateTime.now().minusHours(8));
        data.getProposals().add(proposal);
    }
}
