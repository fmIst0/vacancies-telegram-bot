package com.example.vacanciestelegrambot.service;

import com.example.vacanciestelegrambot.dto.VacancyDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class VacanciesReaderService {

    public List<VacancyDto> getVacanciesFromFile(String fileName) {
        Resource resource = new ClassPathResource(fileName);

        try(InputStreamReader inputStreamReader =
                    new InputStreamReader(
                            resource.getInputStream(),
                            StandardCharsets.UTF_8)
                    ) {
            CsvToBean<VacancyDto> csvToBean = new CsvToBeanBuilder<VacancyDto>(inputStreamReader)
                    .withType(VacancyDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from the file " + fileName, e);
        }
    }
}
