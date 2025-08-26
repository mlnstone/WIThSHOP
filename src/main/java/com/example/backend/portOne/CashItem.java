package com.example.backend.portOne;

import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "cash_item",
        indexes = {@Index(name = "ix_cash_item_merchant_uid", columnList = "merchantUid")},
        uniqueConstraints = {@UniqueConstraint(name = "ux_cash_item_imp_uid", columnNames = "impUid")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CashItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String impUid;

    @Column(nullable = false, length = 64)
    private String merchantUid;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false, length = 20)
    private String status;   // "paid" 등

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}