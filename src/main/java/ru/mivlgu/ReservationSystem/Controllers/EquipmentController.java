package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Equipment;
import ru.mivlgu.ReservationSystem.Services.EquipmentService;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    // Страница со списком всего оборудования
    @GetMapping
    public String listEquipment(Model model) {
        model.addAttribute("equipmentList", equipmentService.getAllEquipment());
        return "equipment/list";  // Вернем страницу с таблицей оборудования
    }

    // Страница для добавления нового оборудования
    @GetMapping("/create")
    public String createEquipmentForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        return "equipment/create";  // Форма для создания нового оборудования
    }

    // Обработка формы добавления нового оборудования
    @PostMapping("/create")
    public String createEquipment(@ModelAttribute Equipment equipment) {
        equipmentService.saveEquipment(equipment);
        return "redirect:/equipment";  // Перенаправляем на страницу списка оборудования
    }

    // Страница для редактирования оборудования
    @GetMapping("/edit/{id}")
    public String editEquipmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("equipment", equipmentService.getEquipmentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid equipment ID: " + id)));
        return "equipment/edit";  // Форма для редактирования
    }

    // Обработка формы редактирования оборудования
    @PostMapping("/edit/{id}")
    public String editEquipment(@PathVariable Long id, @ModelAttribute Equipment equipment) {
        equipment.setEquipmentId(id);
        equipmentService.saveEquipment(equipment);
        return "redirect:/equipment";  // Перенаправляем на страницу списка оборудования
    }

    // Удаление оборудования
    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return "redirect:/equipment";  // Перенаправляем на страницу списка оборудования
    }
}
