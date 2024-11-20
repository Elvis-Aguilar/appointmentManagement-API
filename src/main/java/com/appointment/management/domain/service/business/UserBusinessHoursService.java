package com.appointment.management.domain.service.business;

import com.appointment.management.domain.dto.business.UpdateUserBusinessHours;
import com.appointment.management.domain.dto.business.UserHoursDto;
import com.appointment.management.persistance.entity.BusinessHoursEntity;
import com.appointment.management.persistance.entity.UserBusinessHoursEntity;
import com.appointment.management.persistance.entity.UserEntity;
import com.appointment.management.persistance.repository.AppointmentRepository;
import com.appointment.management.persistance.repository.BusinessHoursRepository;
import com.appointment.management.persistance.repository.UserBusinessHoursRepository;
import com.appointment.management.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBusinessHoursService {


    private final UserBusinessHoursRepository userBusinessHoursRepository;
    private final UserRepository userRepository;
    private final BusinessHoursRepository businessHoursRepository;

    @Transactional
    public void updateUserBusinessHours(UpdateUserBusinessHours updateUserBusinessHours, Long businessHoursId) {
        BusinessHoursEntity businessHours = businessHoursRepository.findById(businessHoursId).
                orElseThrow(() -> new RuntimeException("role not found"));

        //eliminar los empleados de ese horario
        this.userBusinessHoursRepository.deleteAllByBusinessHoursId(businessHours.getId());

        // Asignar nuevos empleados a ese horario
        for (Long usersId: updateUserBusinessHours.users()){
            UserEntity user = userRepository.findById(usersId)
                    .orElseThrow(() -> new RuntimeException("user not found"));

            UserBusinessHoursEntity userBusinessHoursEntity = new UserBusinessHoursEntity(user, businessHours);

            userBusinessHoursRepository.save(userBusinessHoursEntity);
        }
    }

    private UserHoursDto toUserHoursDto(UserEntity user){
        return new UserHoursDto(user.getId(), user.getName(), user.getEmail());
    }

    public List<UserHoursDto> getUserBusinessHours(Long businessHoursId) {
        List<UserBusinessHoursEntity> userBusinessHours = userBusinessHoursRepository.findAllByBusinessHoursId(businessHoursId);

        return userBusinessHours.stream()
                .map(userBusinessH -> this.toUserHoursDto(userBusinessH.getUser()))
                .collect(Collectors.toList());
    }

}
