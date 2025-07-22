package com.MSIL.MSIL_Project.repository;

import com.MSIL.MSIL_Project.entity.Role;
import com.MSIL.MSIL_Project.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(String role);
    List<Role> findByRoleType(RoleType roleType);
    List<Role> findByIsActive(Boolean isActive);
    boolean existsByRole(String role);

    @Query("SELECT r FROM Role r WHERE r.isActive = true ORDER BY r.roleType, r.displayName")
    List<Role> findAllActiveRoles();

    @Query("SELECT r FROM Role r JOIN r.userRoles ur WHERE ur.user.id = :userId AND ur.isActive = true")
    List<Role> findByUserId(@Param("userId") Long userId);
}