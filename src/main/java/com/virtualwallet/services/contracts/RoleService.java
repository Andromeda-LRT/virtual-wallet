package com.virtualwallet.services.contracts;

import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;

import java.util.List;

public interface RoleService {
    Role createRole(User user, Role role);
    void deleteRole(int role_id, User user);
    void updateRole(Role role, User user);
    Role getRole(int role_id, User user);
    List<Role> getAllRoles(User user);
}
