package com.sigma.taskmanagaer.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "staff")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "img_src")
    private String imgSrc;


    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Project> projects;


    @ManyToMany
    @JoinTable(
            name = "assign_task",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns  = @JoinColumn(name = "task_id")
    )
    private Set<Task> tasks;


    public void setProjects(TreeSet<Project> projects) {
        this.projects = projects;
    }

    public void setTasks(TreeSet<Task> tasks) {
        this.tasks = tasks;
    }

}
