package com.laa66.statlyapp.entity;

import com.laa66.statlyapp.model.Image;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "external_id")
    private String externalId;

    private String username;

    private String email;

    @Column(name = "image_url")
    private String image;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_stats_id", referencedColumnName = "id")
    private UserStats userStats;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "friend_id", referencedColumnName = "id")}
    )
    private List<User> following = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = {@JoinColumn(name = "friend_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private List<User> followers = new ArrayList<>();

    public User(long id, String externalId, String username, String email, String image, LocalDateTime joinDate, UserStats userStats) {
        this.id = id;
        this.externalId = externalId;
        this.username = username;
        this.email = email;
        this.image = image;
        this.joinDate = joinDate;
        this.userStats = userStats;
    }

    public void addFollower(User follower) {
        following.add(follower);
    }

    public void removeFollower(User follower) {
        following.remove(follower);
    }
}
