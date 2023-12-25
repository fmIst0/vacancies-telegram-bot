package com.example.vacanciestelegrambot.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class VacancyDto {
    @CsvBindByName(column = "Id")
    private String id;
    @CsvBindByName(column = "Title")
    private String title;
    @CsvBindByName(column = "Short Description")
    private String shortDescription;
    @CsvBindByName(column = "Long Description")
    private String longDescription;
    @CsvBindByName(column = "Company")
    private String company;
    @CsvBindByName(column = "Salary")
    private String salary;
    @CsvBindByName(column = "Link")
    private String link;
}
