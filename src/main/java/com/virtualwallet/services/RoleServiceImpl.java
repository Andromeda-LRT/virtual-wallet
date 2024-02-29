package com.virtualwallet.services;

import com.virtualwallet.exceptions.DuplicateEntityException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.Role;
import com.virtualwallet.models.User;
import com.virtualwallet.repositories.contracts.RoleRepository;
import com.virtualwallet.services.contracts.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.virtualwallet.model_helpers.ModelConstantHelper.UNAUTHORIZED_OPERATION_ERROR_MESSAGE;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(User user, Role role) {
        checkIfAdmin(user);
        roleRepository.create(role);
        return role;
    }

    @Override
    public void deleteRole(int role_id, User user) {
        checkIfAdmin(user);
        Role role = roleRepository.getById(role_id);
        roleRepository.delete(role_id);
    }

    @Override
    public void updateRole(Role role, User user) {
        checkIfAdmin(user);
        boolean duplicateRoleNameExists = true;
        try {
            roleRepository.getByStringField("name", role.getName());
        } catch (EntityNotFoundException e) {
            duplicateRoleNameExists = false;
        }
        if (duplicateRoleNameExists) {
            throw new DuplicateEntityException("Role", "name", role.getName());
        }
        roleRepository.create(role);
    }

    @Override
    public Role getRole(int role_id, User user) {
        checkIfAdmin(user);
        return roleRepository.getById(role_id);
    }

    @Override
    public List<Role> getAllRoles(User user) {
        checkIfAdmin(user);
        return roleRepository.getAll();
    }

    private static void checkIfAdmin(User user) {
        if (!user.getRole().getName().equals("admin")) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }
}
