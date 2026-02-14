package com.neartalk.api.domain.post.entity;

import com.neartalk.api.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @Column(length = 100)
    private String placeName;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<PostImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags = new ArrayList<>();

    @Builder
    public Post(Long groupId, Long authorId, String title, String content, Point location, String placeName) {
        this.groupId = groupId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.placeName = placeName;
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    public void addTag(PostTag tag) {
        this.tags.add(tag);
        tag.setPost(this);
    }
}
