package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.SkillService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    private final ApplicationData data;

    public SkillServiceImpl(ApplicationData data) {
        this.data = data;
    }

    @Override
    public List<Skill> findAll() {
        return List.copyOf(data.getSkills());
    }

    @Override
    public List<Skill> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ValidationException("Potrebno je izabrati bar jednu vestinu.");
        }

        List<Skill> skills = data.getSkills().stream()
                .filter(skill -> ids.contains(skill.getId()))
                .toList();

        if (skills.size() != ids.size()) {
            throw new ValidationException("Jedna ili vise izabranih vestina ne postoje.");
        }

        return skills;
    }
}
