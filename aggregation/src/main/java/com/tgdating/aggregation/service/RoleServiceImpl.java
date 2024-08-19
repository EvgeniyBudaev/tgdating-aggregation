package com.tgdating.aggregation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final UserService userService;
    @Value("${app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    @Override
    public void assignRole(String userId, String roleName) {
        UserResource userResource = userService.getUser(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation roleRepresentation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    @Override
    public void deleteRole(String userId, String roleName) {
        UserResource userResource = userService.getUser(userId);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation roleRepresentation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().remove(Collections.singletonList(roleRepresentation));
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }
}
