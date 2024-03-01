package com.virtualwallet.services.contracts;

import com.virtualwallet.models.Status;
import com.virtualwallet.models.User;

import java.util.List;

public interface StatusService {
    Status createStatus(User user, Status status);
    void deleteStatus(int status_id, User user);
    void updateStatus(Status status, User user);
    Status getStatus(int status_id, User user);
    List<Status> getAllStatuses(User user);
}
