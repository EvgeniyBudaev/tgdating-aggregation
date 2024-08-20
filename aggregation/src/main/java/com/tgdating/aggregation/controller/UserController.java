package com.tgdating.aggregation.controller;

import com.tgdating.aggregation.aspect.LogMethodExecutionTime;
import com.tgdating.aggregation.dto.response.ResponseUserDto;
import com.tgdating.aggregation.model.UserCreateRecord;
import com.tgdating.aggregation.model.UserUpdateRecord;
import com.tgdating.aggregation.service.UserService;
import com.tgdating.aggregation.shared.security.Authorities;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @LogMethodExecutionTime
    public ResponseEntity<ResponseUserDto> createUser(@RequestBody UserCreateRecord userCreateRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userCreateRecord));
    }

    @PutMapping
    @LogMethodExecutionTime
    @PreAuthorize("hasAuthority('" + Authorities.CUSTOMER + "')")
    public ResponseEntity<UserRepresentation> updateUser(@RequestBody UserUpdateRecord userUpdateRecord) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.updateUser(userUpdateRecord));
    }

    @DeleteMapping("/{id}")
    @LogMethodExecutionTime
    @PreAuthorize("hasAuthority('" + Authorities.CUSTOMER + "')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}")
    @LogMethodExecutionTime
    @PreAuthorize("hasAuthority('" + Authorities.CUSTOMER + "')")
    public ResponseEntity<UserRepresentation> getUserDetail(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(id));
    }

    @PutMapping("/{id}/send-verification-email")
    @LogMethodExecutionTime
    public ResponseEntity<?> sendVerificationEmail(@PathVariable String id) {
        userService.sendVerificationEmail(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/forgot-password")
    @LogMethodExecutionTime
    public ResponseEntity<?> forgotPassword(@RequestParam String username) {
        userService.forgotPassword(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/roles")
    @LogMethodExecutionTime
    @PreAuthorize("hasAuthority('" + Authorities.ADMIN + "')")
    public ResponseEntity<?> getUserRoles(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserRoles(id));
    }

    @GetMapping("/{id}/groups")
    @LogMethodExecutionTime
    @PreAuthorize("hasAuthority('" + Authorities.ADMIN + "')")
    public ResponseEntity<?> getUserGroups(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserGroups(id));
    }
}
