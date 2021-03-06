package com.auth.dto;


public class RegisterDTO {

    private String uuid;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String gender;

    private String dateOfBirth;

    private String username;

    private String password;

    private String biography;

    public RegisterDTO() {
    }

    public RegisterDTO(String id, String firstName, String lastName, String email, String phoneNumber, String gender, String dateOfBirth, String username, String password, String biography) {
        this.uuid = id;
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

    public RegisterDTO(String uuid, NewUserDTO newUserDTO) {
        this.uuid = uuid;
        this.firstName = newUserDTO.getFirstName();
        this.lastName = newUserDTO.getLastName();
        this.email = newUserDTO.getEmail();
        this.phoneNumber = newUserDTO.getPhoneNumber();
        this.gender = newUserDTO.getGender();
        this.dateOfBirth = newUserDTO.getDateOfBirth();
        this.username = newUserDTO.getUsername();
        this.password = newUserDTO.getPassword();
        this.biography = newUserDTO.getBiography();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
}
