package com.esun.ecommerce.web.auth.repository;

import com.esun.ecommerce.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByUsernameAndStatus(String username, Byte status);
    Boolean existsByUsername(String username);
}
