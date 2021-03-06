package com.auth.dto;

public class NewUserDTO {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String dateOfBirth;

    private String username;

    private String password;

    private String biography;

    public NewUserDTO() {
    }

    public NewUserDTO(String firstName, String lastName, String email, String phoneNumber, String gender, String dateOfBirth, String username, String password, String biography) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.username = username;
        this.password = password;
        this.biography = biography;
    }

    public NewUserDTO(RegisterDTO registerDTO){
        this.id = registerDTO.getUuid();
        this.firstName = registerDTO.getFirstName();
        this.lastName = registerDTO.getLastName();
        this.email = registerDTO.getEmail();
        this.phoneNumber = registerDTO.getPhoneNumber();
        this.gender = registerDTO.getGender();
        this.dateOfBirth = registerDTO.getDateOfBirth();
        this.username = registerDTO.getUsername();
        this.password = registerDTO.getPassword();
        this.biography = registerDTO.getBiography();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
