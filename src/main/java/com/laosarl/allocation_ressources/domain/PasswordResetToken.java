package com.laosarl.allocation_ressources.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "passwordResetToken")
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, EXPIRATION);
        return cal.getTime();
    }
}
