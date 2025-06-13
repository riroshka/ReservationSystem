package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipment;
import ru.mivlgu.ReservationSystem.Entities.ClassroomEquipmentId;
import ru.mivlgu.ReservationSystem.Entities.Equipment;
import ru.mivlgu.ReservationSystem.Services.ClassroomService;
import ru.mivlgu.ReservationSystem.Services.EquipmentService;

import java.util.List;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;
    private final EquipmentService equipmentService;

    @Autowired
    public ClassroomController(ClassroomService classroomService, EquipmentService equipmentService) {
        this.classroomService = classroomService;
        this.equipmentService = equipmentService;
    }

    // Страница с перечнем всех аудиторий
    @GetMapping
    public String listClassrooms(Model model) {
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
        return "classrooms/list";  // Страница со списком аудиторий
    }

    // Страница добавления новой аудитории
    @GetMapping("/create")
    public String createClassroomForm(Model model) {
        model.addAttribute("classroom", new Classroom());
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        if (equipmentList.isEmpty()) {
            model.addAttribute("error", "No equipment available.");
        } else {
            model.addAttribute("equipmentList", equipmentList);
        } // Передаем список оборудования
        return "classrooms/create";  // Форма для добавления аудитории
    }

    // Обработка добавления новой аудитории
    @PostMapping("/create")
    public String createClassroom(@ModelAttribute Classroom classroom, @RequestParam("equipmentIds") List<Long> equipmentIds) {
        // Сохраняем аудиторию
        classroom = classroomService.saveClassroom(classroom);

        // Добавляем оборудование в аудиторию
        for (Long equipmentId : equipmentIds) {
            Equipment equipment = equipmentService.getEquipmentById(equipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));
            ClassroomEquipment classroomEquipment = new ClassroomEquipment();
            ClassroomEquipmentId id = new ClassroomEquipmentId(classroom.getClassroomId(), equipment.getEquipmentId());
            classroomEquipment.setId(id);
            classroomEquipment.setClassroom(classroom);
            classroomEquipment.setEquipment(equipment);
            classroomEquipment.setQuantity(1);  // Количество по умолчанию
            classroomService.saveClassroomEquipment(classroomEquipment);
        }

        return "redirect:/classrooms";  // Перенаправляем на список аудиторий
    }

    // Страница редактирования аудитории
    @GetMapping("/edit/{id}")
    public String editClassroomForm(@PathVariable Long id, Model model) {
        Classroom classroom = classroomService.getClassroomById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid classroom ID: " + id));
        model.addAttribute("classroom", classroom);
        model.addAttribute("equipmentList", equipmentService.getAllEquipment());  // Список оборудования для выбора
        return "classrooms/edit";  // Страница для редактирования
    }

    // Обработка редактирования аудитории
    @PostMapping("/edit/{id}")
    public String editClassroom(@PathVariable Long id,
                                @ModelAttribute Classroom classroom,
                                @RequestParam(name = "equipmentIds", required = false) List<Long> equipmentIds) {
        classroom.setClassroomId(id);
        classroom = classroomService.saveClassroom(classroom);

        // Если equipmentIds пустой или null, продолжаем без изменений оборудования
        if (equipmentIds != null) {
            // Удаляем старое оборудование, которого нет в новом списке
            classroomService.getClassroomEquipment(id).forEach(classroomEquipment -> {
                if (!equipmentIds.contains(classroomEquipment.getEquipment().getEquipmentId())) {
                    classroomService.deleteClassroomEquipment(id, classroomEquipment.getEquipment().getEquipmentId());
                }
            });

            // Добавляем новое оборудование, если оно еще не добавлено
            for (Long equipmentId : equipmentIds) {
                if (!classroomService.getClassroomEquipment(id).stream().anyMatch(e -> e.getEquipment().getEquipmentId().equals(equipmentId))) {
                    Equipment equipment = equipmentService.getEquipmentById(equipmentId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + equipmentId));

                    ClassroomEquipmentId classroomEquipmentId = new ClassroomEquipmentId(classroom.getClassroomId(), equipment.getEquipmentId());
                    ClassroomEquipment classroomEquipment = new ClassroomEquipment();
                    classroomEquipment.setId(classroomEquipmentId);
                    classroomEquipment.setClassroom(classroom);
                    classroomEquipment.setEquipment(equipment);
                    classroomEquipment.setQuantity(1);  // Количество по умолчанию

                    classroomService.saveClassroomEquipment(classroomEquipment);
                }
            }
        }

        return "redirect:/classrooms";  // Перенаправляем на список аудиторий
    }



    // Удалить аудиторию
    @GetMapping("/delete/{id}")
    public String deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return "redirect:/classrooms";  // Перенаправляем на список аудиторий
    }

    @GetMapping("/filter-classrooms")
    public String filterClassrooms(@RequestParam(name = "capacity", required = false) Integer capacity,
                                   @RequestParam(name = "equipment", required = false) Long equipmentId,
                                   Model model) {
        List<Classroom> classrooms;

        if (capacity != null && equipmentId != null) {
            classrooms = classroomService.findByCapacityAndEquipment(capacity, equipmentId);
        } else if (capacity != null) {
            classrooms = classroomService.findByCapacity(capacity);
        } else if (equipmentId != null) {
            classrooms = classroomService.findByEquipment(equipmentId);
        } else {
            classrooms = classroomService.getAllClassrooms();
        }

        model.addAttribute("classrooms", classrooms);
        model.addAttribute("equipmentList", equipmentService.getAllEquipment()); // Получаем список оборудования
        return "classrooms/classroom-list"; // Страница с отфильтрованными аудиториями
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Classroom>> filterClassrooms(
            @RequestParam int minCapacity,
            @RequestParam int maxCapacity,
            @RequestParam(required = false) List<Long> equipment) {

        List<Classroom> filtered = classroomService.filterClassrooms(
                minCapacity,
                maxCapacity,
                equipment
        );
        return ResponseEntity.ok(filtered);
    }
}
