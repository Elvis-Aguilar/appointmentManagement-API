package com.appointment.management.domain.service.appointmet;

import com.appointment.management.domain.dto.appoinment.CancellationSurchargeDto;
import com.appointment.management.persistance.entity.CancellationSurchargeEntity;
import com.appointment.management.persistance.repository.CancellationSurchargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancellationSurchargeService {

    @Autowired
    private CancellationSurchargeRepository cancellationSurchargeRepository;

    private CancellationSurchargeDto toDto(CancellationSurchargeEntity entity) {
        return new CancellationSurchargeDto(entity.getId(), entity.getAppointment().getId(), entity.getDate(), entity.getCustomer().getId(), entity.getStatus());
    }

    @Transactional
    public CancellationSurchargeDto create(CancellationSurchargeEntity entity) {
        CancellationSurchargeEntity saved = cancellationSurchargeRepository.save(entity);
        return toDto(saved);
    }

}
