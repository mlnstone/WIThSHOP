package com.example.backend.user.entity;

import com.example.backend.cart.entity.Cart;
import com.example.backend.common.base.EntityDate;
import com.example.backend.common.enums.Gender;
import com.example.backend.common.enums.UserProvider;
import com.example.backend.common.enums.UserType;
import com.example.backend.orderHistory.entity.OrderHistory;
import com.example.backend.point.entity.Point;
import com.example.backend.report.entity.Report;
import com.example.backend.review.entity.Review;
import com.example.backend.userCoupon.entity.UserCoupon;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "user_created_at", nullable = false, updatable = false)),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "user_updated_at"))
})
public class User extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String userEmail;
    private String userPwd;
    private String userName;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private UserProvider userProvider;

    private String userProviderId;
    private LocalDateTime userDeletedAt;
    private String birth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phone;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Point point;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<OrderHistory> orderHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    private List<Report> reportsMade;

    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL)
    private List<Report> reportsReceived;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Cart> carts;

}

