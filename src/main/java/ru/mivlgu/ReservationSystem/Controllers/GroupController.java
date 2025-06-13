package ru.mivlgu.ReservationSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mivlgu.ReservationSystem.Entities.Group;
import ru.mivlgu.ReservationSystem.Services.GroupService;

@Controller
@RequestMapping("/admin/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // 1. Список групп
    @GetMapping
    public String listGroups(Model model) {
        model.addAttribute("groups", groupService.getAllGroups());
        return "admin/groups";
    }

    // 2. Форма создания группы
    @GetMapping("/create")
    public String createGroupForm(Model model) {
        model.addAttribute("group", new Group());
        return "admin/create_group";
    }

    // 3. Обработка создания группы
    @PostMapping("/create")
    public String createGroup(@ModelAttribute("group") Group group) {
        groupService.createGroup(group);
        return "redirect:/admin/groups";
    }

    // 4. Форма редактирования группы
    @GetMapping("/edit/{id}")
    public String editGroupForm(@PathVariable("id") Long id, Model model) {
        Group group = groupService.getGroupById(id);
        model.addAttribute("group", group);
        return "admin/edit_group";
    }

    // 5. Обработка редактирования группы
    @PostMapping("/edit/{id}")
    public String updateGroup(@PathVariable("id") Long id, @ModelAttribute("group") Group updatedGroup) {
        groupService.updateGroup(id, updatedGroup);
        return "redirect:/admin/groups";
    }

    // 6. Удаление группы
    @GetMapping("/delete/{id}")
    public String deleteGroup(@PathVariable("id") Long id) {
        groupService.deleteGroup(id);
        return "redirect:/admin/groups";
    }
}
