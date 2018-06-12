package de.markus.statbot.bot;

import de.markus.statbot.model.Message;
import de.markus.statbot.model.Server;
import de.markus.statbot.model.User;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.ServerRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MessageReceivedListener implements IListener<MessageReceivedEvent> {

    private final transient MessageRepository messageRepository;

    private final transient UserRepository userRepository;

    private final transient ServerRepository serverRepository;

    @Autowired
    public MessageReceivedListener(MessageRepository messageRepository, UserRepository userRepository, ServerRepository serverRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        IGuild guild = messageReceivedEvent.getGuild();
        IChannel channel = messageReceivedEvent.getChannel();
        IMessage message = messageReceivedEvent.getMessage();
        IUser author = messageReceivedEvent.getAuthor();
        if (message.getContent().startsWith("!scan")) {
            List<IChannel> channelsMentioned = message.getChannelMentions();
            if (channelsMentioned.size() == 0) {
                channel.sendMessage("Scan running...");
                channel.sendMessage(getData(guild, channel) ? "Scan successful!" : "Scan failed :(");
            } else
                for (IChannel aChannelsMentioned : channelsMentioned) {
                    channel.sendMessage("Scan of " + aChannelsMentioned.getName() + " is running...");
                    channel.sendMessage(getData(guild, aChannelsMentioned) ? "Scan of " + aChannelsMentioned.getName() + " succesful!" : "Scan " + aChannelsMentioned.getName() + " failed :(");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private boolean getData(IGuild guild, IChannel channel) {
        try {
            //Add Guild to database if it doesn't exist already
            Server server = serverRepository.findById(guild.getLongID()).orElse(new Server(guild.getLongID(), guild.getName(), null));
            serverRepository.saveAndFlush(server);
            server.setUsers(userRepository.findAllByServers(server));
            serverRepository.saveAndFlush(server);

            //Get users in guild and add them to the database if they don't exist already
            List<IUser> users = guild.getUsers();
            for (IUser iUser : users) {
                User user = userRepository.findById(iUser.getLongID()).orElse(new User(iUser.getLongID(), iUser.getName()));
                userRepository.saveAndFlush(user);
            }

            //Get all unsaved messages in the channel and add their metadata to the database
            List<IMessage> lobbyExternMessages = channel.getFullMessageHistory();
            log.error("Gefundene: " + lobbyExternMessages.size());
            int i = 0;
            for (IMessage lobbyExternMessage : lobbyExternMessages) {
                User author = userRepository.getOne(lobbyExternMessage.getAuthor().getLongID());
                int length = lobbyExternMessage.getContent().length();
                Date creationDate = Date.from(lobbyExternMessage.getCreationDate());
                //lobbyExternMessage.getMentions();       coming soon
                Message message = messageRepository.findById(lobbyExternMessage.getLongID()).orElse(new Message(lobbyExternMessage.getLongID(), length, author, creationDate));
                messageRepository.saveAndFlush(message);
                i++;
            }
            log.error(i + "");
            return true;
        } catch (NullPointerException e) {
            log.error("NullPointerException was thrown!");
            e.printStackTrace();
            return false;
        }
    }
}
