package com.example.vacanciestelegrambot;

import java.util.ArrayList;
import java.util.List;
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
    private static final int VACANCY_ID_INDEX = 1;
    private static final String EQUAL_SIGN = "=";

    public VacanciesBot(@Value("${telegram.token}") String botToken) {
        super(botToken);
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
            } else if (callbackData.startsWith("vacancyId=")) {
                Long vacancyId = Long.parseLong(callbackData.split(EQUAL_SIGN)[VACANCY_ID_INDEX]);
                showVacancyDescription(vacancyId, update);
            }
        }
    }

    private void showVacancyDescription(Long vacancyId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText("Vacancy description with id = " + vacancyId);
        executeSendMessage(sendMessage);
    }

    private void showJuniorVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Junior vacancy");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getJuniorVacanciesMenu());
        executeSendMessage(sendMessage);
    }

    private void showMiddleVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Middle vacancy");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getMiddleVacanciesMenu());
        executeSendMessage(sendMessage);
    }

    private void showSeniorVacancies(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Please, choose Senior vacancy");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getSeniorVacanciesMenu());
        executeSendMessage(sendMessage);
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

        return buttons;
    }

    private List<InlineKeyboardButton> getJuniorButtons() {
        List<InlineKeyboardButton> juniorButtons = new ArrayList<>();

        InlineKeyboardButton amazonVacancy = new InlineKeyboardButton();
        amazonVacancy.setText("Junior Java Developer at Amazon");
        amazonVacancy.setCallbackData("vacancyId=1");
        juniorButtons.add(amazonVacancy);

        InlineKeyboardButton googleVacancy = new InlineKeyboardButton();
        googleVacancy.setText("Junior Java Developer at Google");
        googleVacancy.setCallbackData("vacancyId=2");
        juniorButtons.add(googleVacancy);

        InlineKeyboardButton metaVacancy = new InlineKeyboardButton();
        metaVacancy.setText("Junior Java Developer at Meta");
        metaVacancy.setCallbackData("vacancyId=3");
        juniorButtons.add(metaVacancy);

        return juniorButtons;
    }

    private List<InlineKeyboardButton> getMiddleButtons() {
        List<InlineKeyboardButton> middleButtons = new ArrayList<>();

        InlineKeyboardButton epamVacancy = new InlineKeyboardButton();
        epamVacancy.setText("Middle Java Software Engineer at Epam");
        epamVacancy.setCallbackData("vacancyId=4");
        middleButtons.add(epamVacancy);

        InlineKeyboardButton dataArtVacancy = new InlineKeyboardButton();
        dataArtVacancy.setText("Middle Java Developer at DataArt");
        dataArtVacancy.setCallbackData("vacancyId=5");
        middleButtons.add(dataArtVacancy);

        return middleButtons;
    }

    private List<InlineKeyboardButton> getSeniorButtons() {
        List<InlineKeyboardButton> seniorButtons = new ArrayList<>();

        InlineKeyboardButton globalLogicVacancy = new InlineKeyboardButton();
        globalLogicVacancy.setText("Senior Java Developer at GlobalLogic");
        globalLogicVacancy.setCallbackData("vacancyId=6");
        seniorButtons.add(globalLogicVacancy);

        return seniorButtons;
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
}
