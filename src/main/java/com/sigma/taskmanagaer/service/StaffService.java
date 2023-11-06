package com.sigma.taskmanagaer.service;

import com.sigma.taskmanagaer.controller.EmployeeController;
import com.sigma.taskmanagaer.controller.ManagerController;
import com.sigma.taskmanagaer.dto.StaffRequest;
import com.sigma.taskmanagaer.dto.StaffResponse;
import com.sigma.taskmanagaer.entity.Role;
import com.sigma.taskmanagaer.entity.Staff;
import com.sigma.taskmanagaer.exp.UserNotFoundException;
import com.sigma.taskmanagaer.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    public List<StaffResponse> findAll (Role role) {
        List<Staff> staff = staffRepository.findByRole(role);
        return staff.stream().map((s) -> mapStaffToStaffResponse(s, role)).toList();
    }

    public StaffResponse findById(long id, Role role) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return mapStaffToStaffResponse(staff, role);
    }

    public StaffResponse save(StaffRequest staffRequest, Role role, long id) throws IOException {

        String imgSrc = "user.jpg";
        Optional<Staff> oldStaff = staffRepository.findById(id);

        if (oldStaff.isPresent()) {
            imgSrc = oldStaff.get().getImgSrc();
        }

        if (staffRequest.getImg() != null && !(staffRequest.getImg().isEmpty())) {
            imgSrc = fileService.saveImg(staffRequest.getImg(), staffRequest.getPhoneNo());
        }

        Staff staff = Staff.builder()
                .staffId(id)
                .firstName(staffRequest.getFirstName())
                .lastName(staffRequest.getLastName())
                .email(staffRequest.getEmail())
                .password(passwordEncoder.encode(staffRequest.getPassword()))
                .phoneNo(staffRequest.getPhoneNo())
                .imgSrc(imgSrc)
                .role(role)
                .build();

        Staff savedStaff = staffRepository.save(staff);
        return mapStaffToStaffResponse(savedStaff, role);
    }


    public void deleteById(long id) {
        staffRepository.deleteById(id);
    }

    private StaffResponse mapStaffToStaffResponse(Staff staff, Role role) {

        StaffResponse staffResponse = StaffResponse.builder()
                .staffId(staff.getStaffId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phoneNo(staff.getPhoneNo())
                .imgSrc(staff.getImgSrc())
                .build();



        if (role == Role.ROLE_PROJECT_MANAGER) {
            staffResponse.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ManagerController.class)
                            .getManagerById(staff.getStaffId())).withSelfRel());

            staffResponse.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ManagerController.class)
                            .getManagerProjects(staff.getStaffId())
            ).withRel("projects"));
        }

        if (role == Role.ROLE_EMPLOYEE) {
            staffResponse.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EmployeeController.class)
                            .getEmployeeById(staff.getStaffId())).withSelfRel());

            staffResponse.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(EmployeeController.class)
                            .getEmployeeTasks(staff.getStaffId())
            ).withRel("tasks"));
        }

        return staffResponse;

    }


}
