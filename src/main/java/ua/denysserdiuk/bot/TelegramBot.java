package denysserdiuk.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import denysserdiuk.model.Budget;
import denysserdiuk.model.UserSession;
import denysserdiuk.repository.UserRepository;
import denysserdiuk.service.BudgetLinesService;
import denysserdiuk.enums.SessionStage;

import java.time.LocalDate;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String apiKey;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetLinesService budgetLinesService;

    private Map<Long, UserSession> userSessions = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = null;
        String messageText = null;
        String callbackData = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId();
            messageText = update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            callbackData = update.getCallbackQuery().getData();
        }

        if (chatId != null) {
            UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());

            if (messageText != null) {
                handleIncomingMessage(chatId, messageText, session);
            } else if (callbackData != null) {
                handleCallbackQuery(chatId, callbackData, session);
            }
        }
    }

    private void handleIncomingMessage(Long chatId, String messageText, UserSession session) {
        switch (session.getStage()) {
            case AWAITING_USERNAME:
                session.setUsername(messageText);
                sendMessage(chatId, "Please enter your password:");
                session.setStage(SessionStage.AWAITING_PASSWORD);
                break;
            case AWAITING_PASSWORD:
                authenticateUser(chatId, session.getUsername(), messageText, session);
                break;
            case AWAITING_DESCRIPTION:
                session.getBudget().setDescription(messageText);
                sendMessage(chatId, "Please enter category:");
                session.setStage(SessionStage.AWAITING_CATEGORY);
                break;
            case AWAITING_CATEGORY:
                session.getBudget().setCategory(messageText);
                sendMessage(chatId, "Please enter the amount:");
                session.setStage(SessionStage.AWAITING_AMOUNT);
                break;
            case AWAITING_AMOUNT:
                handleBudgetAmount(chatId, messageText, session);
                break;
            default:
                if ("/start".equals(messageText)) {
                    sendLoginButton(chatId);
                } else {
                    sendMessage(chatId, "Please use the /start command to begin.");
                }
                break;
        }
    }

    private void handleCallbackQuery(Long chatId, String callbackData, UserSession session) {
        if (!session.isAuthenticated()) {
            if ("login".equals(callbackData)) {
                askForUsername(chatId, session);
            }
        } else {
            if ("add_profit".equals(callbackData) || "add_loss".equals(callbackData)) {
                initiateBudget(chatId, session, callbackData);
            }
        }
    }

    private void sendLoginButton(Long chatId) {
        sendInlineKeyboard(chatId, "Please log in to continue:", "Login", "login");
    }

    private void askForUsername(Long chatId, UserSession session) {
        sendMessage(chatId, "Please enter your username:");
        session.setStage(SessionStage.AWAITING_USERNAME);
    }

    private void authenticateUser(Long chatId, String username, String password, UserSession session) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authenticationManager.authenticate(authToken);
            if (auth.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(auth);
                session.setAuthenticated(true);
                session.setUser(userRepository.findByUsername(username));
                sendMessage(chatId, "Login successful!");
                sendActionButtons(chatId);
            } else {
                sendMessage(chatId, "Authentication failed. Please try again.");
                sendLoginButton(chatId);
                session.reset();
            }
        } catch (Exception e) {
            sendMessage(chatId, "Invalid username or password.");
            sendLoginButton(chatId);
            session.reset();
        }
    }

    private void initiateBudget(Long chatId, UserSession session, String type) {
        Budget budget = new Budget();
        budget.setType(type.equals("add_profit") ? "profit" : "loss");
        budget.setUser(session.getUser());
        session.setBudget(budget);
        sendMessage(chatId, "Please enter a description for the " + budget.getType() + ":");
        session.setStage(SessionStage.AWAITING_DESCRIPTION);
    }

    private void handleBudgetAmount(Long chatId, String messageText, UserSession session) {
        try {
            double amount = Double.parseDouble(messageText);
            Budget budget = session.getBudget();
            budget.setAmount(amount);
            budget.setDate(LocalDate.now());
            budgetLinesService.addBudgetLine(budget);

            sendMessage(chatId, "Budget line added successfully!");
            sendActionButtons(chatId);
            session.resetBudget();
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Invalid amount. Please enter a valid number.");
        }
    }

    private void sendActionButtons(Long chatId) {
        sendInlineKeyboard(chatId, "Would you like to add another profit or loss?",
                "Add Profit", "add_profit",
                "Add Loss", "add_loss");
    }

    private void sendInlineKeyboard(Long chatId, String text, String... buttonPairs) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int i = 0; i < buttonPairs.length; i += 2) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(InlineKeyboardButton.builder()
                    .text(buttonPairs[i])
                    .callbackData(buttonPairs[i + 1])
                    .build());
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "CountUpDns_bot";
    }

    @Override
    public String getBotToken() {
        return apiKey;
    }
}
