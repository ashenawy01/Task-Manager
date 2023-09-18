package com.sigma.taskmanagaer.repository;

import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByRole(Role role);

}
