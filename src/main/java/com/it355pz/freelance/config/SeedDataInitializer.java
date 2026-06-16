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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
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

        Category webDevelopment = new Category(data.nextCategoryId(), "Web projekti",
                "Izrada i odrzavanje poslovnih web resenja.");
        Category design = new Category(data.nextCategoryId(), "Dizajn",
                "Vizuelni identitet i uredjenje korisnickog iskustva.");
        Category content = new Category(data.nextCategoryId(), "Sadrzaj",
                "Priprema tekstova, prezentacija i materijala za objavu.");
        data.getCategories().addAll(List.of(webDevelopment, design, content));

        Skill webPages = new Skill(data.nextSkillId(), "Web stranice");
        Skill copywriting = new Skill(data.nextSkillId(), "Pisanje tekstova");
        Skill visualDesign = new Skill(data.nextSkillId(), "Vizuelni dizajn");
        Skill organization = new Skill(data.nextSkillId(), "Organizacija sadrzaja");
        Skill userExperience = new Skill(data.nextSkillId(), "Korisnicko iskustvo");
        data.getSkills().addAll(List.of(webPages, copywriting, visualDesign, organization, userExperience));

        User client = new User(data.nextUserId(), "client_milan", "client123", "Milan Petrovic",
                "milan@example.com", UserRole.CLIENT, "Vlasnik malog biznisa koji objavljuje poslove.");
        User secondClient = new User(data.nextUserId(), "client_jelena", "client123", "Jelena Simic",
                "jelena@example.com", UserRole.CLIENT, "Menadzerka koja trazi saradnike za digitalne projekte.");
        User freelancer = new User(data.nextUserId(), "freelancer_ana", "freelancer123", "Ana Markovic",
                "ana@example.com", UserRole.FREELANCER, "Freelancer za organizaciju sadrzaja i poslovne stranice.");
        User secondFreelancer = new User(data.nextUserId(), "freelancer_marko", "freelancer123", "Marko Jovanovic",
                "marko@example.com", UserRole.FREELANCER, "Freelancer za dizajn, tekstove i korisnicko iskustvo.");
        data.getUsers().addAll(List.of(client, secondClient, freelancer, secondFreelancer));

        FreelanceJob landingPage = new FreelanceJob(data.nextJobId(), "Izrada prezentacione stranice",
                "Potrebna je moderna prezentaciona stranica za mali studio, sa jasnim opisom usluga i kontakt sekcijom.",
                new BigDecimal("450.00"), webDevelopment, List.of(webPages, organization, userExperience), client,
                LocalDateTime.now().minusDays(2));
        FreelanceJob dashboardDesign = new FreelanceJob(data.nextJobId(), "Redizajn korisnickog panela",
                "Potrebno je urediti pregledan panel za pracenje narudzbina, poruka i statusa saradnje.",
                new BigDecimal("300.00"), design, List.of(visualDesign, userExperience), client,
                LocalDateTime.now().minusDays(1));
        FreelanceJob productTexts = new FreelanceJob(data.nextJobId(), "Priprema tekstova za usluge",
                "Potrebni su kratki i jasni tekstovi za opis usluga, najcesca pitanja i kontakt stranicu.",
                new BigDecimal("180.00"), content, List.of(copywriting, organization), secondClient,
                LocalDateTime.now().minusHours(18));
        data.getJobs().addAll(List.of(landingPage, dashboardDesign, productTexts));

        Path sampleCvPath = createSampleCvFile();
        CvAttachment sampleCv = new CvAttachment(data.nextCvAttachmentId(), "ana-markovic-cv.pdf",
                "application/pdf", sampleCvPath.toFile().length(), sampleCvPath.toString());
        data.getCvAttachments().add(sampleCv);

        Proposal proposal = new Proposal(data.nextProposalId(), landingPage, freelancer,
                "Mogu da pripremim jasnu strukturu stranice, uredim sadrzaj i isporucim pregledno resenje.",
                new BigDecimal("420.00"), 5, sampleCv, LocalDateTime.now().minusHours(8));
        data.getProposals().add(proposal);
    }

    private Path createSampleCvFile() {
        Path path = Path.of("uploads/cv/sample-ana-markovic-cv.pdf").toAbsolutePath().normalize();
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.writeString(path, "%PDF-1.4 sample CV placeholder for seed data");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Seed CV fajl nije mogao da bude kreiran.", ex);
        }
        return path;
    }
}
