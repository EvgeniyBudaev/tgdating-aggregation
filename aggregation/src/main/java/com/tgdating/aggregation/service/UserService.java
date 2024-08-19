package com.tgdating.aggregation.service;

import com.tgdating.aggregation.model.UserCreateRecord;
import com.tgdating.aggregation.model.UserUpdateRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface UserService {
    UserRepresentation createUser(UserCreateRecord userCreateRecord);
    UserRepresentation updateUser(UserUpdateRecord userUpdateRecord);
    void deleteUser(String userId);
    UserResource getUser(String userId);
    UserRepresentation getUserDetail(String userId);
    void sendVerificationEmail(String userId);
    void forgotPassword(String username);
    List<RoleRepresentation> getUserRoles(String userId);
    List<GroupRepresentation> getUserGroups(String userId);
}
