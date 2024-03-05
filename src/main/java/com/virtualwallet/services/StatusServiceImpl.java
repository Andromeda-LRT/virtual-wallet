package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Status;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.StatusRepository;
import com.virtualwallet.services.contracts.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

@Service
public class StatusServiceImpl implements StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Status createStatus(User user, Status status) {
        checkIfAdmin(user);
        statusRepository.create(status);
        return status;
    }

    @Override
    public void deleteStatus(int status_id, User user) {
        checkIfAdmin(user);
        statusRepository.delete(status_id);
    }

    @Override
    public void updateStatus(Status status, User user) {
        checkIfAdmin(user);
        boolean duplicateStatusNameExists = true;
        try {
            statusRepository.getByStringField("name", status.getName());
        } catch (EntityNotFoundException e) {
            duplicateStatusNameExists = false;
        }
        if (duplicateStatusNameExists) {
            throw new DuplicateEntityException("Status", "name", status.getName());
        }
        statusRepository.update(status);
    }

    @Override
    public Status getStatus(int status_id) {
        return statusRepository.getById(status_id);
    }

    @Override
    public List<Status> getAllStatuses() {
        return statusRepository.getAll();
    }

    private static void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
