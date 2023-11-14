package com.sigma.taskmanagaer.repository;

import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByRole(Role role);
    Optional<Staff> findByStaffIdAndRole(Long id, Role role);
    List<Staff> findByRoleIn(List<Role> roles);

    Optional<Staff> findByEmail(String email);

    Optional<Staff> findByResetPasswordToken(String token);

}
