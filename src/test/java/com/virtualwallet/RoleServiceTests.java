package com.virtualwallet;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.RoleRepository;
import com.virtualwallet.services.RoleServiceImpl;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private User adminUser;
    private User regularUser;
    private Role newRole;

    @BeforeEach
    public void setUp() {
        Role userRole = new Role(1, "user");
        Role adminRole = new Role(2, "admin");


        regularUser = new User();
        regularUser.setId(2);
        regularUser.setUsername("regularUser");
        regularUser.setRole(userRole);

        adminUser = new User();
        adminUser.setId(1);
        adminUser.setUsername("adminUser");
        adminUser.setRole(adminRole);

        newRole = new Role(3, "newRole");
    }

    @Test
    public void whenCreateRoleAsAdmin_thenRoleIsCreated() {
        // When
        roleService.createRole(adminUser, newRole);

        // Then
        Mockito.verify(roleRepository, Mockito.times(1)).create(newRole);
    }

    @Test
    public void whenCreateRoleAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> roleService.createRole(regularUser, newRole));

        Mockito.verify(roleRepository, Mockito.never()).create(Mockito.any(Role.class));
    }

    @Test
    public void whenDeleteRoleAsAdmin_thenRoleIsDeleted() {
        // When
        roleService.deleteRole(3, adminUser);

        // Then
        Mockito.verify(roleRepository, Mockito.times(1)).delete(3);
    }

    @Test
    public void whenDeleteRoleAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> roleService.deleteRole(3, regularUser));

        Mockito.verify(roleRepository, never()).delete(3);
    }

    @Test
    public void whenUpdateRoleAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> roleService.updateRole(newRole, regularUser));

        Mockito.verify(roleRepository, never()).create(newRole);
    }

    @Test
    public void whenUpdateRoleWithDuplicateName_thenThrowsDuplicateEntityException() {
        // Arrange
        Mockito.when(roleRepository.getByStringField("name", newRole.getName())).thenReturn(newRole);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> roleService.updateRole(newRole, adminUser));
    }

    @Test
    public void whenGetRole_thenRoleIsReturned() {
        // When
        roleService.getRole(3);

        // Then
        Mockito.verify(roleRepository, Mockito.times(1)).getById(3);
    }

    @Test
    public void whenGetAllRolesAsAdmin_thenAllRolesAreReturned() {
        // When
        roleService.getAllRoles(adminUser);

        // Then
        Mockito.verify(roleRepository, Mockito.times(1)).getAll();
    }

    @Test
    public void whenGetAllRolesAsNonAdmin_thenThrowUnauthorizedOperationException() {
        // Then
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> roleService.getAllRoles(regularUser));

        Mockito.verify(roleRepository, never()).getAll();
    }
}
