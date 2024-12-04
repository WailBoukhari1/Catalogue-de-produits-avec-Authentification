package com.youcode.product_manage.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.youcode.product_manage.dto.request.UserRequest;
import com.youcode.product_manage.dto.response.UserResponse;
import com.youcode.product_manage.entity.Role;
import com.youcode.product_manage.entity.User;

@Mapper(componentModel = "spring", imports = {Collectors.class, Set.class})
public interface UserMapper {
    
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest request);
    
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);
    
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
            .map(Role::name)
            .collect(Collectors.toSet());
    }
}
