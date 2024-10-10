package com.appointment.management.presentation.mapper.helpers;

import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMapperHelper {

    @Autowired
    private UserRepository userRepository;


    public UserEntity findById(Long id) {
        return id != null ? userRepository.findById(id).orElse(null) : null;
    }


    public Long toId(UserEntity user) {
        return user != null ? user.getId() : null;
    }
}