package ru.mivlgu.ReservationSystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipmentId;
import ru.mivlgu.ReservationSystem.Entities.Equipment;
import ru.mivlgu.ReservationSystem.Repositories.ClassroomEquipmentRepository;
import ru.mivlgu.ReservationSystem.Repositories.ClassroomRepository;
import ru.mivlgu.ReservationSystem.Repositories.EquipmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomEquipmentRepository classroomEquipmentRepository;
    private final EquipmentRepository equipmentRepository;

    @Autowired
    public ClassroomService(ClassroomRepository classroomRepository, ClassroomEquipmentRepository classroomEquipmentRepository, EquipmentRepository equipmentRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomEquipmentRepository = classroomEquipmentRepository;
        this.equipmentRepository = equipmentRepository;
    }

    // Получить все аудитории
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // Получить аудиторию по ID
    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    // Сохранить аудиторию
    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    // Удалить аудиторию
    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }

    // Сохранить оборудование в аудитории
    public ClassroomEquipment saveClassroomEquipment(ClassroomEquipment classroomEquipment) {
        return classroomEquipmentRepository.save(classroomEquipment);
    }

    // Получить оборудование в аудитории
    public List<ClassroomEquipment> getClassroomEquipment(Long classroomId) {
        return classroomEquipmentRepository.findAll().stream()
                .filter(classroomEquipment -> classroomEquipment.getClassroom().getClassroomId().equals(classroomId))
                .toList();
    }

    // Удалить оборудование из аудитории
    public void deleteClassroomEquipment(Long classroomId, Long equipmentId) {
        classroomEquipmentRepository.deleteById(new ClassroomEquipmentId(classroomId, equipmentId));
    }

    public List<Classroom> findByCapacity(Integer capacity) {
        return classroomRepository.findByCapacityLessThanEqual(capacity);
    }

    public List<Classroom> findByEquipment(Long equipmentId) {
        return classroomRepository.findByEquipmentList_Equipment_EquipmentId(equipmentId);
    }

    public List<Classroom> findByCapacityAndEquipment(Integer capacity, Long equipmentId) {
        return classroomRepository.findByCapacityLessThanEqualAndEquipmentList_Equipment_EquipmentId(capacity, equipmentId);
    }

    public List<Classroom> filterClassrooms(int minCapacity, int maxCapacity, List<Long> equipmentIds) {
        System.out.println("Min Capacity: " + minCapacity + ", Max Capacity: " + maxCapacity);
        System.out.println("Equipment IDs: " + equipmentIds);
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return classroomRepository.findByCapacityBetween(minCapacity, maxCapacity);
        }
        return classroomRepository.findByCapacityAndEquipment(minCapacity, maxCapacity, equipmentIds);
    }
    /*
    public List<ClassroomEquipment> getClassroomEquipment(Long classroomId) {
        return classroomEquipmentRepository.findByClassroom_ClassroomId(classroomId);
    }

     */
}
