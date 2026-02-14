package com.neartalk.api.domain.group.repository;

import com.neartalk.api.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
