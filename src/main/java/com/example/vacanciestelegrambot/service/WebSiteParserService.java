package com.example.vacanciestelegrambot.service;

import static org.jsoup.Jsoup.connect;

import com.example.vacanciestelegrambot.dto.VacancyDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class WebSiteParserService {
    private static final String MAIN_RESOURCE_LINK = "https://jobs.dou.ua/vacancies/?category=Java";
    private static final String SALARY_CSS_SELECTOR
            = "#container > div.content-wrap > div > div.row.m-db > "
            + "div.cell.m-db > div > div.l-vacancy > div.sh-info > span.salary";
    private static final String LONG_DESCRIPTION_CSS_SELECTOR
            = "#container > div.content-wrap > div > "
            + "div.row.m-db > div.cell.m-db > div > div.l-vacancy";
    private static final String REPEATED_SELECTOR_PART = "#vacancyListId > ul > li:nth-child(";
    private static final String VACANCIES_LIST_CSS_SELECTOR = "li.l-vacancy";

    public List<VacancyDto> getVacanciesFromSite() {
        try {
            List<VacancyDto> allVacancies = new ArrayList<>();

            int vacancyId = 1;

            int childNumber = 1;

            Document document = connect(MAIN_RESOURCE_LINK).get();
            Elements vacanciesElements = document.select(VACANCIES_LIST_CSS_SELECTOR);
            for (Element vacanciesElement : vacanciesElements) {
                String vacancyLink = vacanciesElement
                        .select(REPEATED_SELECTOR_PART + childNumber + ") > div.title > a")
                        .attr("href");
                String title = vacanciesElement
                        .select(REPEATED_SELECTOR_PART + childNumber + ") > div.title > a")
                        .text();
                String shortDescription = vacanciesElement
                        .select(REPEATED_SELECTOR_PART + childNumber + ") > div.sh-info")
                        .text();
                String company = vacanciesElement
                        .select(REPEATED_SELECTOR_PART + childNumber + ") > div.title > strong > a")
                        .text();
                Document vacancyDocument = connect(vacancyLink).get();
                Elements vacancyElements = vacancyDocument.select("#container > div.content-wrap");
                for (Element vacancyElement : vacancyElements) {
                    String longDescription = vacancyElement
                            .select(LONG_DESCRIPTION_CSS_SELECTOR)
                            .text();
                    String salary = vacancyElement
                            .select(SALARY_CSS_SELECTOR)
                            .text();

                    VacancyDto newVacancy = new VacancyDto()
                            .setId(String.valueOf(vacancyId++))
                            .setTitle(title)
                            .setShortDescription(shortDescription)
                            .setLongDescription(longDescription)
                            .setCompany(company)
                            .setSalary(salary)
                            .setLink(vacancyLink);
                    allVacancies.add(newVacancy);

                    childNumber++;
                }
            }
            return allVacancies;
        } catch (IOException e) {
            throw new RuntimeException("Can't parse data from site.", e);
        }
    }
}
