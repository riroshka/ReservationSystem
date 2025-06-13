package ru.mivlgu.ReservationSystem.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mivlgu.ReservationSystem.Entities.Group;
import ru.mivlgu.ReservationSystem.Repositories.GroupRepository;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
    }
    public void createGroup(Group group) {
        groupRepository.save(group);
    }

    public void updateGroup(Long id, Group updatedGroup) {
        Group existingGroup = getGroupById(id);
        existingGroup.setName(updatedGroup.getName());
        groupRepository.save(existingGroup);
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
