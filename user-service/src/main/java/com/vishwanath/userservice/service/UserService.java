package com.vishwanath.userservice.service;

import com.vishwanath.userservice.dto.UserDto;
import com.vishwanath.userservice.exception.UnauthorizedException;
import com.vishwanath.userservice.exception.UserNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;

public  interface UserService {
    List<UserDto> getAllUsers();

    @Transactional
    String deleteUser(Integer id) throws UnauthorizedException, UserNotFoundException;

    @Transactional
    String updateUser(UserDto userDto);
}
