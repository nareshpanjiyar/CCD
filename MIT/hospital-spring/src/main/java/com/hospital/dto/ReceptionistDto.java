package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;

public class ReceptionistDto {

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    private String password; // optional when editing

    @NotBlank(message = "Full name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String email;
}