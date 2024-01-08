package com.example.vacanciestelegrambot;

import com.example.vacanciestelegrambot.dto.VacancyDto;
import com.example.vacanciestelegrambot.service.VacancyService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@PropertySources({
        @PropertySource("classpath:tgconfig.properties"),
        @PropertySource("classpath:application.properties")
})
public class VacanciesBot extends TelegramLongPollingBot {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final int VACANCY_ID_INDEX = 1;
    private static final String EQUAL_SIGN = "=";
    private final VacancyService vacancyService;

    private final Map<Long, String> lastShownVacancyLevel = new HashMap<>();

    @Autowired
    public VacanciesBot(@Value("${telegram.token}") String botToken,
                        VacancyService vacancyService) {
        super(botToken);
        this.vacancyService = vacancyService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null) {
            handleStartCommand(update);
        }
        if (update.getCallbackQuery() != null) {
            String callbackData = update.getCallbackQuery().getData();

            if ("showJuniorVacancies".equals(callbackData)) {
                showJuniorVacancies(update);
            } else if ("showMiddleVacancies".equals(callbackData)) {
                showMiddleVacancies(update);
            } else if ("showSeniorVacancies".equals(callbackData)) {
                showSeniorVacancies(update);
            } else if ("showOtherVacancies".equals(callbackData)) {
                showOtherVacancies(update);
            } else if (callbackData.startsWith("vacancyId=")) {
                String vacancyId = callbackData.split(EQUAL_SIGN)[VACANCY_ID_INDEX];
                showVacancyDescription(vacancyId, update);
            } else if ("backToVacancies".equals(callbackData)) {
                handleBackToVacanciesCommand(update);
            } else if ("backToStartMenu".equals(callbackData)) {
                handleBackToStartCommand(update);
            }
        }
    }

    private void handleBackToStartCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText("Choose title:");
        sendMessage.setReplyMarkup(getStartMenu());
        executeSendMessage(sendMessage);
    }

    private void handleBackToVacanciesCommand(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String level = lastShownVacancyLevel.get(chatId);

        if ("junior".equals(level)) {
            showJuniorVacancies(update);
        } else if ("middle".equals(level)) {
            showMiddleVacancies(update);
        } else if ("senior".equals(level)) {
            showSeniorVacancies(update);
        } else if ("other".equals(level)) {
            showOtherVacancies(update);
        }
    }

    private void showVacancyDescription(String vacancyId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        String fullVacancyDescription = createFullVacancyDescription(vacancyId);
        sendMessage.setText(fullVacancyDescription);
        sendMessage.setReplyMarkup(getBackToVacanciesMenu());
        executeSendMessage(sendMessage);
    }

    private ReplyKeyboard getBackToVacanciesMenu() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton backToVacanciesButton = new InlineKeyboardButton();
        backToVacanciesButton.setText("Back to vacancies");
        backToVacanciesButton.setCallbackData("backToVacancies");
        buttons.add(backToVacanciesButton);

        InlineKeyboardButton backToStartMenu = new InlineKeyboardButton();
        backToStartMenu.setText("Back to start menu");
        backToStartMenu.setCallbackData("backToStartMenu");
        buttons.add(backToStartMenu);

        return createKeyboard(buttons);
    }

    private void showOtherVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose vacancy");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getOtherVacanciesMenu());
        executeSendMessage(sendMessage);

        lastShownVacancyLevel.put(chatId, "other");
    }

    private void showJuniorVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Junior vacancy");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getJuniorVacanciesMenu());
        executeSendMessage(sendMessage);

        lastShownVacancyLevel.put(chatId, "junior");
    }

    private void showMiddleVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Middle vacancy");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        executeSendMessage(sendMessage);

        lastShownVacancyLevel.put(chatId, "middle");
    }

    private void showSeniorVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Senior vacancy");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());
        executeSendMessage(sendMessage);

        lastShownVacancyLevel.put(chatId, "senior");
    }

    private ReplyKeyboard getJuniorVacanciesMenu() {
        return createKeyboard(getJuniorButtons());
    }

    private ReplyKeyboard getMiddleVacanciesMenu() {
        return createKeyboard(getMiddleButtons());
    }

    private ReplyKeyboard getSeniorVacanciesMenu() {
        return createKeyboard(getSeniorButtons());
    }

    private ReplyKeyboard getOtherVacanciesMenu() {
        return createKeyboard(getOtherVacanciesButtons());
    }

    private void handleStartCommand(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Welcome to vacancies bot! Please, choose your title:");
        sendMessage.setReplyMarkup(getStartMenu());
        executeSendMessage(sendMessage);
    }

    private ReplyKeyboard getStartMenu() {
        return createKeyboard(getTitleButtons());
    }

    @Override
    public String getBotUsername() {
        return "vacancies bot";
    }

    private List<InlineKeyboardButton> getVacanciesButtons(List<VacancyDto> vacancies) {
        List<InlineKeyboardButton> vacanciesButtons = new ArrayList<>();

        for (VacancyDto vacancyDto : vacancies) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancyDto.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + vacancyDto.getId());
            vacanciesButtons.add(vacancyButton);
        }

        return vacanciesButtons;
    }

    private List<InlineKeyboardButton> getTitleButtons() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        InlineKeyboardButton junior = new InlineKeyboardButton();
        junior.setText("Junior");
        junior.setCallbackData("showJuniorVacancies");
        buttons.add(junior);

        InlineKeyboardButton middle = new InlineKeyboardButton();
        middle.setText("Middle");
        middle.setCallbackData("showMiddleVacancies");
        buttons.add(middle);

        InlineKeyboardButton senior = new InlineKeyboardButton();
        senior.setText("Senior");
        senior.setCallbackData("showSeniorVacancies");
        buttons.add(senior);

        InlineKeyboardButton other = new InlineKeyboardButton();
        other.setText("Others");
        other.setCallbackData("showOtherVacancies");
        buttons.add(other);

        return buttons;
    }

    private List<InlineKeyboardButton> getOtherVacanciesButtons() {
        List<VacancyDto> otherVacancies = vacancyService.getOtherVacancies();

        return getVacanciesButtons(otherVacancies);
    }

    private List<InlineKeyboardButton> getJuniorButtons() {
        List<VacancyDto> juniorVacancies = vacancyService.getJuniorVacancies();

        return getVacanciesButtons(juniorVacancies);
    }

    private List<InlineKeyboardButton> getMiddleButtons() {
        List<VacancyDto> middleVacancies = vacancyService.getMiddleVacancies();

        return getVacanciesButtons(middleVacancies);
    }

    private List<InlineKeyboardButton> getSeniorButtons() {
        List<VacancyDto> seniorVacancies = vacancyService.getSeniorVacancies();

        return getVacanciesButtons(seniorVacancies);
    }

    private ReplyKeyboard createKeyboard(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(List.of(buttons));
        return keyboard;
    }

    private void executeSendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Execution failed!!!");
        }
    }

    private String createFullVacancyDescription(String vacancyId) {
        String company = vacancyService.getVacancyById(vacancyId).getCompany();
        String shortDescription = vacancyService.getVacancyById(vacancyId).getShortDescription();
        String longDescription = vacancyService.getVacancyById(vacancyId).getLongDescription();
        String salary = vacancyService.getVacancyById(vacancyId).getSalary();
        String link = vacancyService.getVacancyById(vacancyId).getLink();
        return new StringBuilder()
                .append(company).append(LINE_SEPARATOR)
                .append(shortDescription).append(LINE_SEPARATOR)
                .append(longDescription).append(LINE_SEPARATOR)
                .append(salary).append(LINE_SEPARATOR)
                .append(link).append(LINE_SEPARATOR)
                .toString();
    }
}
