package com.sigma.taskmanagaer.repository;

import com.sigma.taskmanagaer.entity.Project;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByEmployee(Staff staff);
    List<Task> findByProject(Project project);
}
