package com.sigma.taskmanagaer.repository;


import com.sigma.taskmanagaer.entity.Project;
import com.sigma.taskmanagaer.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManager(Staff staff);

}
