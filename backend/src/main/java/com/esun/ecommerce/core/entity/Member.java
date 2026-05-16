package com.esun.ecommerce.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @ToString.Exclude
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Column(name = "inptime", nullable = false, updatable = false)
    private LocalDateTime inptime;

    @Column(name = "updtime", nullable = false)
    private LocalDateTime updtime;

    @PrePersist
    protected void onCreate() {
        inptime = updtime = LocalDateTime.now();
        if (status == null) {
            status = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updtime = LocalDateTime.now();
    }
}
