package de.markus.statbot.bots.discord;

import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.Discord4JHandler;
import de.markus.statbot.bots.discord.commands.CollectCommand;
import de.markus.statbot.bots.discord.commands.ScanCommand;
import de.markus.statbot.repositories.ChannelRepository;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.ServerRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class DiscordBot {

    @Autowired
    private transient MessageRepository messageRepository;

    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient ServerRepository serverRepository;

    @Autowired
    private transient ChannelRepository channelRepository;

    @PostConstruct
    public void init() {
        IDiscordClient client = createClient("token", true);
        //Commands
        assert client != null;
        CommandHandler cmdHandler = new Discord4JHandler(client);
        cmdHandler.registerCommand(new ScanCommand(messageRepository, channelRepository, userRepository, serverRepository));
        cmdHandler.registerCommand(new CollectCommand(messageRepository, channelRepository, userRepository, serverRepository));
    }

    private static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            return null;
        }
    }

}
