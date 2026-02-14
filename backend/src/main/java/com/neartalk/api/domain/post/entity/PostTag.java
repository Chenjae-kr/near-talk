package com.neartalk.api.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private Post post;

    @Column(nullable = false, length = 30)
    private String name;

    @Builder
    public PostTag(String name) {
        this.name = name;
    }
}
