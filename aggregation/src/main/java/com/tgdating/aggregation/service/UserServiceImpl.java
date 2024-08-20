package com.tgdating.aggregation.service;

import com.tgdating.aggregation.dto.response.ResponseUserDto;
import com.tgdating.aggregation.model.UserCreateRecord;
import com.tgdating.aggregation.model.UserUpdateRecord;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;
    @Value("${app.keycloak.defaultUserGroup}")
    private String defaultUserGroup;

    @Override
    public ResponseUserDto createUser(UserCreateRecord userCreateRecord) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userCreateRecord.username());
        userRepresentation.setEmail(userCreateRecord.email());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setFirstName(userCreateRecord.firstName());
        userRepresentation.setLastName(userCreateRecord.lastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userCreateRecord.password());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));

        UsersResource usersResource = getUsersResource();
        Response response = usersResource.create(userRepresentation);
        log.info("Status code {}", response.getStatus());

        if (!Objects.equals(201, response.getStatus())) {
            throw new RuntimeException("Status code " + response.getStatus());
        }

        log.info("New user has been created");
        List<UserRepresentation> userRepresentations = usersResource
                .searchByUsername(userCreateRecord.username(), true);
        UserRepresentation user = userRepresentations.get(0);

        UserResource userResource = usersResource.get(user.getId());
        userResource.joinGroup(defaultUserGroup);

        RoleRepresentation customerRole = keycloak.realm(realm).roles().get("customer").toRepresentation();
        userResource.roles().realmLevel().add((Collections.singletonList(customerRole)));

        // TODO: расскоментировать после создания и проверки почты
//        sendVerificationEmail(user.getId());

        ResponseUserDto responseUserDto = new ResponseUserDto();
        responseUserDto.setId(user.getId());
        responseUserDto.setUsername(user.getUsername());
        responseUserDto.setFirstName(user.getFirstName());
        responseUserDto.setLastName(user.getLastName());
        responseUserDto.setEmail(user.getEmail());
        responseUserDto.setIsEnabled(user.isEnabled());
        responseUserDto.setIsEmailVerified(user.isEmailVerified());

        return responseUserDto;
    }

    @Override
    public UserRepresentation updateUser(UserUpdateRecord userUpdateRecord) {
        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(userUpdateRecord.id());
        List<UserRepresentation> userRepresentations = usersResource
                .searchByUsername(userUpdateRecord.username(), true);
        UserRepresentation user = userRepresentations.get(0);
        user.setFirstName(userUpdateRecord.firstName());
        user.setLastName(userUpdateRecord.lastName());
        userResource.update(user);
        return userResource.toRepresentation();
    }

    @Override
    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.delete(userId);
    }

    @Override
    public UserResource getUser(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    @Override
    public UserRepresentation getUserDetail(String userId) {
        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(userId);
        return userResource.toRepresentation();
    }

    @Override
    public void sendVerificationEmail(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    @Override
    public void forgotPassword(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        UserRepresentation userRepresentation = userRepresentations.get(0);
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    @Override
    public List<RoleRepresentation> getUserRoles(String userId) {
        return getUser(userId).roles().realmLevel().listAll();
    }

    @Override
    public List<GroupRepresentation> getUserGroups(String userId) {
        return getUser(userId).groups();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }
}
