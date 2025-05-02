package com.dtbonthego.profileservice.security.services;

import com.dtbonthego.profileservice.model.ERole;
import com.dtbonthego.profileservice.model.Role;
import com.dtbonthego.profileservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void build_ShouldCreateUserDetailsWithCorrectFields() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(Role.builder().id(1).name(ERole.ROLE_CUSTOMER).build());
        roles.add(Role.builder().id(2).name(ERole.ROLE_ADMIN).build());
        
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("user@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("1234567890")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(roles)
                .build();

        // Act
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // Assert
        assertEquals(user.getId(), userDetails.getId());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getEmail(), userDetails.getEmail());
        assertEquals(user.getPassword(), userDetails.getPassword());
        
        // Check authorities
        Set<String> expectedRoles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        
        Set<String> actualRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        assertEquals(expectedRoles, actualRoles);
        assertTrue(actualRoles.contains("ROLE_CUSTOMER"));
        assertTrue(actualRoles.contains("ROLE_ADMIN"));
    }

    @Test
    void isAccountNonExpired_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void equals_WithSameId_ShouldReturnTrue() {
        // Arrange
        UserDetailsImpl user1 = new UserDetailsImpl(
                1L, "user1", "user1@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        UserDetailsImpl user2 = new UserDetailsImpl(
                1L, "user2", "user2@example.com", "different", 
                Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        
        // Act & Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ShouldReturnFalse() {
        // Arrange
        UserDetailsImpl user1 = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        UserDetailsImpl user2 = new UserDetailsImpl(
                2L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertNotEquals(user1, user2);
    }

    @Test
    void equals_WithNull_ShouldReturnFalse() {
        // Arrange
        UserDetailsImpl user = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertNotEquals(user, null);
    }

    @Test
    void equals_WithDifferentClass_ShouldReturnFalse() {
        // Arrange
        UserDetailsImpl user = new UserDetailsImpl(
                1L, "user", "user@example.com", "password", 
                Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        
        // Act & Assert
        assertNotEquals(user, "Not a UserDetailsImpl object");
    }
} 