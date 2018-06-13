package de.markus.statbot.bot;

import de.markus.statbot.model.Channel;
import de.markus.statbot.model.Message;
import de.markus.statbot.model.Server;
import de.markus.statbot.model.User;
import de.markus.statbot.repositories.ChannelRepository;
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

    private final transient ChannelRepository channelRepository;

    @Autowired
    public MessageReceivedListener(MessageRepository messageRepository, UserRepository userRepository, ServerRepository serverRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void handle(MessageReceivedEvent messageReceivedEvent) {
        IGuild guild = messageReceivedEvent.getGuild();
        IChannel channel = messageReceivedEvent.getChannel();
        IMessage message = messageReceivedEvent.getMessage();
        IUser author = messageReceivedEvent.getAuthor();

        //Gigantic if condition for commands
        if (message.getContent().startsWith("!scan")) {
            List<IChannel> channelsMentioned = message.getChannelMentions();
            if (channelsMentioned.size() == 0) {
                channel.sendMessage("Scan running...");
                channel.sendMessage(collectMessages(channel) ? "Scan successful!" : "Scan failed :(");
            } else
                for (IChannel aChannelsMentioned : channelsMentioned) {
                    channel.sendMessage("Scan of " + aChannelsMentioned.getName() + " is running...");
                    channel.sendMessage(collectMessages(aChannelsMentioned) ? "Scan of " + aChannelsMentioned.getName() + " succesful!" : "Scan " + aChannelsMentioned.getName() + " failed :(");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private boolean collectGuildData(IGuild guild) {
        try {
            //Add Guild to database if it doesn't exist already
            Server server = serverRepository.findById(guild.getLongID()).orElse(new Server(guild.getLongID(), guild.getName(), null));
            serverRepository.saveAndFlush(server);
            server.setUsers(userRepository.findAllByServers(server));
            serverRepository.saveAndFlush(server);

            //Get users in guild and add them to the database if they don't already exist
            List<IUser> users = guild.getUsers();
            for (IUser iUser : users) {
                User user = userRepository.findById(iUser.getLongID()).orElse(new User(iUser.getLongID(), iUser.getName()));
                userRepository.saveAndFlush(user);
            }

            //Get channels in guild and add them to the database if they don't already exist
            List<IChannel> channels = guild.getChannels();
            for (IChannel aChannel : channels) {
                Channel bChannel = channelRepository.findById(aChannel.getLongID()).orElse(new Channel(aChannel.getLongID(), aChannel.getName(), serverRepository.getOne(guild.getLongID())));
                channelRepository.saveAndFlush(bChannel);
            }
            return true;
        } catch (Exception e){
            log.error("Exception in collectGuildData was thrown!");
            e.printStackTrace();
            return false;
        }
    }

    private boolean collectMessages(IChannel channel) {
        try {
            //Get all unsaved messages in the channel and add their metadata to the database
            List<IMessage> messages = messageRepository.countByChannelId(channelRepository.getOne(channel.getLongID())) > 0
                    ? channel.getMessageHistoryTo(messageRepository.findByMaxCreationDate().toInstant())
                    : channel.getFullMessageHistory();
            for (IMessage iMessage : messages) {
                User author = userRepository.getOne(iMessage.getAuthor().getLongID());
                int length = iMessage.getContent().length();
                Date creationDate = Date.from(iMessage.getCreationDate());
                //iMessage.getMentions();       coming soon
                Message message = messageRepository.findById(iMessage.getLongID()).
                        orElse(new Message(iMessage.getLongID(), length, author, creationDate, channelRepository.getOne(channel.getLongID())));
                messageRepository.saveAndFlush(message);
            }
            return true;
        } catch (Exception e) {
            log.error("Exception in collectMessage was thrown!");
            e.printStackTrace();
            return false;
        }
    }
}
