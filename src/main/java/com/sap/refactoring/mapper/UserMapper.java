package com.sap.refactoring.mapper;

import com.sap.refactoring.dto.request.UserRequest;
import com.sap.refactoring.dto.response.UserResponse;
import com.sap.refactoring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    void updateUser(UserRequest userRequest, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest request);

    UserResponse toResponse(User user);
}


