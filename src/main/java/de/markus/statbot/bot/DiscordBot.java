package de.markus.statbot.bot;

import de.markus.statbot.model.Message;
import de.markus.statbot.model.User;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DiscordBot {

    @Autowired
    private transient MessageRepository messageRepository;

    @Autowired
    private transient UserRepository userRepository;

    @PostConstruct
    public void init() {
        IDiscordClient client = createClient("token", true); // Gets the client object (from the first example)
        //Events
        EventDispatcher dispatcher = client.getDispatcher(); // Gets the EventDispatcher instance for this client instance
        dispatcher.registerListener((IListener<MessageReceivedEvent>) messageReceivedEvent -> {
            IMessage message = messageReceivedEvent.getMessage();
            IChannel channel = messageReceivedEvent.getChannel();
            if (message.getContent().equals("!scan")) {
                channel.sendMessage("Scan running...");
                channel.sendMessage(getData(client) ? "Scan successful!" : "Scan failed :(");
            }
            if (message.getContent().equals("!shutdown")) {

            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getData(client);
    }

    private boolean getData(IDiscordClient client) {
        try {
            //Finde den Server!
            IGuild guild = client.getGuildByID(443443108244619274L);

            //Finde die User!
            List<IUser> users = guild.getUsers();

            //Füge die User der Datenbank hinzu!
            for (IUser iUser : users) {
                User user = userRepository.findById(iUser.getLongID()).orElse(new User(iUser.getLongID()));
                userRepository.saveAndFlush(user);
            }

            //Finde den Channel!
            IChannel lobbyExtern = client.getChannelByID(443450540592988162L);

            //Finde die Nachrichten!
            log.error(lobbyExtern.toString());
            List<IMessage> lobbyExternMessages;
            try {
                lobbyExternMessages = lobbyExtern.getMessageHistoryTo(messageRepository.findByMaxCreationDate().toInstant());
            } catch (NullPointerException e) {
                lobbyExternMessages = lobbyExtern.getFullMessageHistory();
            }
            for (IMessage lobbyExternMessage : lobbyExternMessages) {
                User author = userRepository.getOne(lobbyExternMessage.getAuthor().getLongID());
                int length = lobbyExternMessage.getContent().length();
                Date creationDate = Date.from(lobbyExternMessage.getCreationDate());
                //lobbyExternMessage.getMentions();       coming soon
                Message message = messageRepository.findById(lobbyExternMessage.getLongID()).orElse(new Message(lobbyExternMessage.getLongID(), length, author, creationDate));
                messageRepository.saveAndFlush(message);
            }
            return true;
        } catch (NullPointerException e) {
            log.error("NullPointerException gefunden!");
            //e.printStackTrace();
            return false;
        }
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
