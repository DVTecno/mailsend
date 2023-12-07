package com.mail.entity;

import com.mail.enumerated.Roles;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String dni;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Roles rol;
    private String verificationCode;
    private String resetPasswordToken;


}
