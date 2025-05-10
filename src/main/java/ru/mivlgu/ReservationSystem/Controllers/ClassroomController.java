package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Classroom;
import ru.mivlgu.ReservationSystem.Services.ClassroomService;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @GetMapping
    public String listClassrooms(Model model) {
        model.addAttribute("classrooms", classroomService.getAllClassrooms());
        return "classrooms/list";
    }

    @GetMapping("/{id}")
    public String viewClassroom(@PathVariable Long id, Model model) {
        model.addAttribute("classroom", classroomService.getClassroomById(id).orElse(null));
        return "classrooms/view";
    }

    @GetMapping("/create")
    public String createClassroomForm(Model model) {
        model.addAttribute("classroom", new Classroom());
        return "classrooms/create";
    }

    @PostMapping("/create")
    public String createClassroom(@ModelAttribute Classroom classroom) {
        classroomService.saveClassroom(classroom);
        return "redirect:/classrooms";
    }

    @GetMapping("/equipment")
    public String listEquipment(Model model) {
        model.addAttribute("equipment", classroomService.getAllEquipment());
        return "equipment/list";
    }

    @GetMapping("/equipment/{id}")
    public String viewEquipment(@PathVariable Long id, Model model) {
        model.addAttribute("equipment", classroomService.getEquipmentById(id).orElse(null));
        return "equipment/view";
    }
}
