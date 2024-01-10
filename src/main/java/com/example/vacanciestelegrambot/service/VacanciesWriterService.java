package com.example.vacanciestelegrambot.service;

import com.example.vacanciestelegrambot.dto.VacancyDto;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class VacanciesWriterService {

    public void writeVacanciesToFile(List<VacancyDto> vacancyDtos, String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = {"Id", "Title", "Short Description", "Long Description", "Company", "Salary", "Link"};
            writer.writeNext(header);

            for (VacancyDto vacancyDto : vacancyDtos) {
                String[] data = {
                        vacancyDto.getId(),
                        vacancyDto.getTitle(),
                        vacancyDto.getShortDescription(),
                        vacancyDto.getLongDescription(),
                        vacancyDto.getCompany(),
                        vacancyDto.getSalary(),
                        vacancyDto.getLink()
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't write data to the file " + fileName, e);
        }
    }
}
