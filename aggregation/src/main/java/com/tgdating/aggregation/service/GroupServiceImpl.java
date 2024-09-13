package com.tgdating.aggregation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
    private final UserService userService;

    @Override
    public void assignGroup(String userId, String groupId) {
        UserResource userResource = userService.getUser(userId);
        userResource.joinGroup(groupId);
    }

    @Override
    public void deleteGroup(String userId, String groupId) {
        UserResource userResource = userService.getUser(userId);
        userResource.leaveGroup(groupId);
    }
}
