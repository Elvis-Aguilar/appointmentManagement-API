package com.appointment.management.presentation.mapper.helpers;

import com.appointment.management.persistance.entity.BusinessConfigurationEntity;
import com.appointment.management.persistance.repository.BusinessConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessConfigurationMapperHelper {

    @Autowired
    private BusinessConfigurationRepository businessConfigurationRepository;


    public BusinessConfigurationEntity findById(Long id) {
        return id != null ? this.businessConfigurationRepository.findById(id).orElse(null) : null;
    }


    public Long toId(BusinessConfigurationEntity businessConfiguration) {
        return businessConfiguration != null ? businessConfiguration.getId() : null;
    }
}
