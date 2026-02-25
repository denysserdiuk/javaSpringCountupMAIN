package denysserdiuk.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {
    //test
    @Autowired
    private TelegramBot telegramBot;

    @Bean
    TelegramBotsApi  telegramBotsApi() throws TelegramApiException{
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try{
            telegramBotsApi.registerBot(telegramBot);
        }catch (TelegramApiException e){
            System.err.println("Failed to register Telegram bot: " + e.getMessage());
            throw e;
        }
        return telegramBotsApi;
    }
}
