package com.learnclaude.library.author;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String bio;

    protected Author() {}

    public Author(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }

    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
}
