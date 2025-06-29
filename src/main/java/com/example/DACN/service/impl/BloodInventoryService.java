package com.example.DACN.service.impl;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.BloodInventoryDTO;
import com.example.DACN.exception.OurException;
import com.example.DACN.model.Appointment;
import com.example.DACN.model.BloodInventory;
import com.example.DACN.model.BloodRegistration;
import com.example.DACN.model.Event;
import com.example.DACN.model.status.AppointmentStatus;
import com.example.DACN.repository.AppointmentRepo;
import com.example.DACN.repository.BloodInventoryRepo;
import com.example.DACN.repository.BloodRegistrationRepository;
import com.example.DACN.repository.EventRepo;
import com.example.DACN.service.interfac.IBloodInventoryService;
import com.example.DACN.service.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class BloodInventoryService implements IBloodInventoryService {
    @Autowired
    private BloodInventoryRepo bloodInventoryRepo;

    @Autowired
    private BloodRegistrationRepository
            bloodRegistrationRepository;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Override
    public ApiResponse addNew(String donationType, int quantity, LocalDateTime lastUpdate, LocalDateTime expirationTime, Long appointmentId){
        ApiResponse response = new ApiResponse();
        try{
            donationType = donationType.toUpperCase();
            var app = appointmentRepo.findById(appointmentId);
            if (app.get().getBloodInventory() != null){
                response.setCode(404);
                response.setMessage("Dữ liệu đã tồn tại!!");
                return response;
            }else if ("CONFIRMED".equalsIgnoreCase(app.get().getStatus().name())){
                BloodInventory bI = new BloodInventory();
                bI.setBloodType(donationType.toUpperCase());
                bI.setQuantity(quantity);
                bI.setLastUpdated(lastUpdate);
                bI.setExpirationDate(expirationTime);

                bI.setAppointment(app.get());
                bloodInventoryRepo.save(bI);
                BloodInventoryDTO dto = Utils.mapBloodInventoryToDTO(bI);
                var app1= app.get();
                app1.setBloodInventory(bI);
                app1.setStatus(AppointmentStatus.COMPLETED);
                appointmentRepo.save(app1);
                response.setCode(200);
                response.setMessage("Successful");
                response.setBloodInventoryDTO(dto);

                BloodRegistration bloodRegistration = bloodRegistrationRepository.findByEventId(app1.getEvent().getEventId()).orElseThrow();
                if (donationType.equals("O")) {
                    bloodRegistration.setRegBloodO(bloodRegistration.getRegBloodO()+1);
                }
                if (donationType.equals("A")) {
                    bloodRegistration.setRegBloodA(bloodRegistration.getRegBloodA()+1);
                }
                if (donationType.equals("B")) {
                    bloodRegistration.setRegBloodB(bloodRegistration.getRegBloodB()+1);
                }
                if (donationType.equals("AB")) {
                    bloodRegistration.setRegBloodAB(bloodRegistration.getRegBloodAB()+1);
                }

                bloodRegistrationRepository.save(bloodRegistration);
            }
            else {
                response.setCode(404);
                response.setMessage("Bạn không có quyền thêm vì trạng thái cuộc hẹn không phù hợp");
                return response;
            }




        }catch(OurException e){
            response.setCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setCode(500);
            response.setMessage(e.getMessage());

        }
        return response;
    }

    @Override
    public  ApiResponse getAll(){
        ApiResponse response = new ApiResponse();
        try{
            List<BloodInventory> bloodInventories = bloodInventoryRepo.findAll();
            if (!bloodInventories.isEmpty()){
                List<BloodInventoryDTO> dto = Utils.mapListBloodInventoryToDTO(bloodInventories);
                response.setCode(200);
                response.setMessage("Successful");
                response.setBloodInventoryDTOList(dto);
                return response;
            }
            else{
                response.setCode(404);
                response.setMessage("Danh sach rong");
                return response;
            }

        }catch(OurException e){
            response.setCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ApiResponse get(Long id){
        ApiResponse response = new ApiResponse();
        try{
            var bI = bloodInventoryRepo.findById(id).orElseThrow(()->new OurException("Không tìm thấy thông tin hiến máu "));
            BloodInventoryDTO dto = Utils.mapBloodInventoryToDTO(bI);
            response.setCode(200);
            response.setMessage("Successful");
            response.setBloodInventoryDTO(dto);
            return response;

        }catch(OurException e){
            response.setCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResponse update(Long id, String bloodType, int quantity,  LocalDateTime expirationDate, Long appointmentId) {
        ApiResponse response = new ApiResponse();
        try {
            // 1. Find the blood inventory entry; throw exception if not found
            var existed = bloodInventoryRepo.findById(id)
                    .orElseThrow(() -> new OurException("Không tìm thấy thông tin hiến máu với ID: " + id));

            // 2. Validate input parameters
            validateBloodInventoryUpdateInputs(bloodType, quantity, expirationDate, appointmentId);

            // 3. Find the appointment (if appointmentId is provided)
            var appointment = appointmentRepo.findById(appointmentId)
                    .orElseThrow(() -> new OurException("Không tìm thấy lịch hẹn với ID: " + appointmentId));

            // 4. Update the BloodInventory entity with new values
            existed.setBloodType(bloodType);
            existed.setQuantity(quantity);
            existed.setLastUpdated(LocalDateTime.now());
            existed.setExpirationDate(expirationDate);

            existed.setAppointment(appointment);

            // 5. Convert the updated entity to a DTO and update the response
            var dto = Utils.mapBloodInventoryToDTO(existed);
            response.setCode(200);
            response.setMessage("Cập nhật thông tin hiến máu thành công.");
            response.setBloodInventoryDTO(dto);

        } catch (OurException e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        } catch (IllegalArgumentException e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Lỗi hệ thống: " + e.getMessage());
        }
        return response;
    }


    public ApiResponse delete(Long id){
        ApiResponse response = new ApiResponse();
        try{
            bloodInventoryRepo.deleteById(id);
            response.setCode(200);
            response.setMessage("successfully");
        }catch(OurException e){
            response.setCode(404);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ApiResponse removeBloodInventory(Long id) {
        ApiResponse response = new ApiResponse();
        try {
            BloodInventory bloodInventory = bloodInventoryRepo.findById(id)
                    .orElseThrow(() -> new OurException("BloodInventory with ID " + id + " not found"));

            Appointment appointment = appointmentRepo.findByBloodInventoryId(id);
            if (appointment != null) {
                appointmentRepo.delete(appointment);
            }

            // Now delete the BloodInventory record
            bloodInventoryRepo.deleteById(id);


        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());

        }

        return response;
    }

    private void validateBloodInventoryUpdateInputs(String bloodType, int quantity, LocalDateTime expirationDate, Long appointmentId) {
        if (bloodType == null || bloodType.isEmpty()) {
            throw new IllegalArgumentException("Loại máu không được để trống.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng máu phải là số dương lớn hơn 0.");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("Ngày hết hạn không được để trống.");
        }
        if (appointmentId == null || appointmentId <= 0) {
            throw new IllegalArgumentException("ID lịch hẹn không hợp lệ.");
        }
    }

}
