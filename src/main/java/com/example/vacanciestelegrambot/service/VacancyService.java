package com.example.vacanciestelegrambot.service;

import com.example.vacanciestelegrambot.dto.VacancyDto;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VacancyService {
    private final VacanciesReaderService vacanciesReaderService;
    private final Map<String, VacancyDto> vacancies = new HashMap<>();

    @Autowired
    public VacancyService(VacanciesReaderService vacanciesReaderService) {
        this.vacanciesReaderService = vacanciesReaderService;
    }

    @PostConstruct
    public void init() {
        List<VacancyDto> vacanciesDtos = vacanciesReaderService.getVacanciesFromFile("vacancies.csv");
        for (VacancyDto vacancy : vacanciesDtos) {
            vacancies.put(vacancy.getId(), vacancy);
        }
    }

    public List<VacancyDto> getJuniorVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDto -> vacancyDto.getTitle().toLowerCase().contains("junior"))
                .toList();
    }

    public List<VacancyDto> getMiddleVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDto -> vacancyDto.getTitle().toLowerCase().contains("middle"))
                .toList();
    }

    public List<VacancyDto> getSeniorVacancies() {
        return vacancies.values().stream()
                .filter(vacancyDto -> vacancyDto.getTitle().toLowerCase().contains("senior"))
                .toList();
    }

    public List<VacancyDto> getOtherVacancies() {
        Predicate<VacancyDto> otherVacanciesPredicate =
                vacancyDto -> !(vacancyDto.getTitle().toLowerCase().contains("junior")
                || vacancyDto.getTitle().toLowerCase().contains("middle")
                || vacancyDto.getTitle().toLowerCase().contains("senior"));

        return vacancies.values().stream()
                .filter(otherVacanciesPredicate)
                .toList();
    }

    public VacancyDto getVacancyById(String vacancyId) {
        return vacancies.values().stream()
                .filter(vacancyDto -> vacancyDto.getId().equals(vacancyId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can't find vacancy with id = " + vacancyId));
    }
}
