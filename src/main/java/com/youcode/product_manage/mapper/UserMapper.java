package com.youcode.product_manage.mapper;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.entity.User;
import com.youcode.product_manage.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", constant = "true")
    User toEntity(UserRequest request);
    
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);
    
    @Mapping(target = "roles", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
    
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    }
}
