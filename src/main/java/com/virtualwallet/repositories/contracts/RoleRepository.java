package com.virtualwallet.repositories.contracts;

import com.virtualwallet.models.Role;

import java.util.List;

public interface RoleRepository {
    void create(Role entity);

    void update(Role entity);

    void delete(int id);

    List<Role> getAll();

    Role getById(int id);

    Role getByStringField(String fieldName);
}
