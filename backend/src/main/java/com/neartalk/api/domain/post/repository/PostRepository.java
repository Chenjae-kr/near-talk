package com.neartalk.api.domain.post.repository;

import com.neartalk.api.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByGroupId(Long groupId, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);
}
