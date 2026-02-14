package com.neartalk.api.domain.group.entity;

import com.neartalk.api.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 500)
    private String description;

    private String imageUrl;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(nullable = false)
    private int memberCount = 1;

    @Column(nullable = false)
    private Long ownerId;

    @Builder
    public Group(String name, String description, String imageUrl, String region, Long ownerId) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.region = region;
        this.ownerId = ownerId;
    }

    public void incrementMemberCount() {
        this.memberCount++;
    }

    public void decrementMemberCount() {
        if (this.memberCount > 0) {
            this.memberCount--;
        }
    }
}
