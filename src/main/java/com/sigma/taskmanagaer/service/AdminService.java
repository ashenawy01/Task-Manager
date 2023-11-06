package com.sigma.taskmanagaer.service;

import com.sigma.taskmanagaer.dto.AdminRequest;
import com.sigma.taskmanagaer.dto.AdminResponse;
import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.exp.UserNotFoundException;
import com.sigma.taskmanagaer.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final StaffRepository staffRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    public List<AdminResponse> findAll () {
        List<Staff> staff = staffRepository.findByRoleIn(List.of(Role.ROLE_ADMIN, Role.ROLE_GLOBAL_ADMIN));
        return staff.stream().map(this::mapStaffToAdminResponse).toList();
    }

    public AdminResponse findById(long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return mapStaffToAdminResponse(staff);
    }

    public AdminResponse save(AdminRequest adminRequest, long id) throws IOException {

        String imgSrc = "user.jpg";
        Role role;

        if (id == 0) {
            role = adminRequest.isGlobal() ? Role.ROLE_GLOBAL_ADMIN : Role.ROLE_ADMIN;
        } else {
            Staff oldAdmin = staffRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Admin Not Found"));
            role = oldAdmin.getRole();
            imgSrc = oldAdmin.getImgSrc();
        }

        if (adminRequest.getImg() != null && !(adminRequest.getImg().isEmpty())) {
            imgSrc = fileService.saveImg(adminRequest.getImg(), adminRequest.getPhoneNo());
        }
        
        Staff staff = Staff.builder()
                .staffId(id)
                .firstName(adminRequest.getFirstName())
                .lastName(adminRequest.getLastName())
                .email(adminRequest.getEmail())
                .password(passwordEncoder.encode(adminRequest.getPassword()))
                .phoneNo(adminRequest.getPhoneNo())
                .imgSrc(imgSrc)
                .role(role)
                .build();


        Staff savedStaff = staffRepository.save(staff);
        return mapStaffToAdminResponse(savedStaff);
    }


    public void deleteById(long id) {
        staffRepository.deleteById(id);
    }


    private AdminResponse mapStaffToAdminResponse(Staff staff) {

        AdminResponse adminResponse = AdminResponse.builder()
                .staffId(staff.getStaffId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phoneNo(staff.getPhoneNo())
                .imgSrc(staff.getImgSrc())
                .isGlobal(staff.getRole() == Role.ROLE_GLOBAL_ADMIN)
                .build();


        return adminResponse;

    }


}
