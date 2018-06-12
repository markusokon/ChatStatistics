package de.markus.statbot.bot;

import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.ServerRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
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

    @PostConstruct
    public void init() {
        IDiscordClient client = createClient("token", true);
        //Events
        assert client != null;
        EventDispatcher dispatcher = client != null ? client.getDispatcher() : null; // Gets the EventDispatcher instance for this client instance
        dispatcher.registerListener(new MessageReceivedListener(messageRepository,userRepository,serverRepository));
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
