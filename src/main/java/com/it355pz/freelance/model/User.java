package com.it355pz.freelance.model;

public class User {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String profileSummary;

    public User() {
    }

    public User(Long id, String username, String fullName, String email, UserRole role, String profileSummary) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.profileSummary = profileSummary;
    }

    public boolean isFreelancer() {
        return UserRole.FREELANCER.equals(role);
    }

    public boolean isClient() {
        return UserRole.CLIENT.equals(role);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public void setProfileSummary(String profileSummary) {
        this.profileSummary = profileSummary;
    }
}
