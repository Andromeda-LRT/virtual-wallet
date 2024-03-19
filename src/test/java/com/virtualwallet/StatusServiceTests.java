package com.virtualwallet;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.Status;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.StatusRepository;
import com.virtualwallet.services.StatusServiceImpl;
import com.virtualwallet.services.contracts.StatusService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTests {
    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusServiceImpl statusService;
    private User adminUser;
    private User regularUser;

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
    }

    @Test
    public void createStatus_Should_CreateStatus_IfUserIsAdmin() {
        // Arrange
        Status status = new Status(4, "New Status");

        // Act
        Status createdStatus = statusService.createStatus(adminUser, status);

        // Assert
        Assertions.assertEquals(status, createdStatus);
    }

    @Test
    public void createStatus_Should_ThrowUnauthorizedOperationException_IfUserIsNotAdmin() {
        // Arrange
        Status status = new Status(4, "New Status");

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> statusService.createStatus(regularUser, status));
        Mockito.verify(statusRepository, Mockito.never()).create(status);
    }

    @Test
    public void deleteStatus_Should_ThrowUnauthorizedOperationException_IfUserIsNotAdmin() {
        // Arrange
        int status_id = 4;

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> statusService.deleteStatus(status_id, regularUser));
        Mockito.verify(statusRepository, Mockito.never()).delete(status_id);
    }

    @Test
    public void deleteStatus_Should_DeleteStatus_IfUserIsAdmin() {
        // Arrange
        int status_id = 4;

        // Act
        statusService.deleteStatus(status_id, adminUser);

        // Assert
        Mockito.verify(statusRepository, Mockito.times(1)).delete(status_id);
    }

    @Test
    public void updateStatus_Should_ThrowUnauthorizedOperationException_IfUserIsNotAdmin() {
        // Arrange
        Status status = new Status(4, "New Status");

        // Act & Assert
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> statusService.updateStatus(status, regularUser));
        Mockito.verify(statusRepository, Mockito.never()).update(status);
    }

    @Test
    public void updateStatus_Should_ThrowDuplicateEntityException_IfStatusNameExists() {
        // Arrange
        Status status = new Status(4, "duplicate");
        Mockito.when(statusRepository.getByStringField("name", status.getName())).thenReturn(status);

        // Act & Assert
        Assertions.assertThrows(DuplicateEntityException.class, () -> statusService.updateStatus(status, adminUser));
        Mockito.verify(statusRepository, Mockito.never()).update(status);
    }

    @Test
    public void updateStatus_Should_UpdateStatus_IfUserIsAdmin() {
        // Arrange
        Status status = new Status(4, "New Status");
        Mockito.when(statusRepository.getByStringField("name", status.getName()))
                .thenThrow(EntityNotFoundException.class);
        // Act
        statusService.updateStatus(status, adminUser);

        // Assert
        Mockito.verify(statusRepository, Mockito.times(1)).update(status);
    }

    @Test
    public void getStatus_Should_ReturnStatus() {
        // Arrange
        Status status = new Status(4, "New Status");
        Mockito.when(statusRepository.getById(4)).thenReturn(status);

        // Act
        Status result = statusService.getStatus(4);

        // Assert
        Assertions.assertEquals(status, result);
    }

    @Test
    public void getAllStatuses_Should_ReturnAllStatuses() {
        // Arrange
        List<Status> statuses = new ArrayList<>();
        statuses.add(new Status(1, "Status 1"));
        statuses.add(new Status(2, "Status 2"));
        statuses.add(new Status(3, "Status 3"));
        Mockito.when(statusRepository.getAll()).thenReturn(statuses);

        // Act
        List<Status> result = statusService.getAllStatuses();

        // Assert
        Assertions.assertEquals(statuses, result);
    }
}


