package com.maveric.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Id
    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String employeeId;
    private String bloodGroup;
    private String email;
    private String address;

}
