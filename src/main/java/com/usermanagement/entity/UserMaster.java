package com.usermanagement.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "USER_MASTER")
@Data
public class UserMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String fullName;
    private String email;
    private Long mobile;
    private String gender;
    private LocalDate dob;
    private Long ssn;
    private String password;
    private String accountStatus;
    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private LocalDate createDate;
    @Column(name = "update_date",insertable = false)
    @UpdateTimestamp
    private LocalDate updateDate;
    private String createdBy;
    private String updatedBy;

}
