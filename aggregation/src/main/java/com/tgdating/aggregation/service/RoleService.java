package com.tgdating.aggregation.service;

public interface RoleService {
    void assignRole(String userId, String roleName);

    void deleteRole(String userId, String roleName);
}
