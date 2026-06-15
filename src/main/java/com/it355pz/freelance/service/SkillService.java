package com.it355pz.freelance.service;

import com.it355pz.freelance.model.Skill;

import java.util.List;

public interface SkillService {

    List<Skill> findAll();

    List<Skill> findAllByIds(List<Long> ids);
}
