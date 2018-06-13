package de.markus.statbot.bots.discord.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.markus.statbot.model.Channel;
import de.markus.statbot.model.Message;
import de.markus.statbot.model.User;
import de.markus.statbot.repositories.ChannelRepository;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.ServerRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ScanCommand implements CommandExecutor {

    private final transient MessageRepository messageRepository;
    private final transient ChannelRepository channelRepository;
    private final transient UserRepository userRepository;
    private final transient ServerRepository serverRepository;

    @Autowired
    public ScanCommand(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository, ServerRepository serverRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
    }

    @Command(aliases = "!scan")
    public void onCommand(IGuild guild, IMessage message, IChannel channel, String[] args) {
        List<IChannel> channelsMentioned = message.getChannelMentions();
        if (args.length == 1 && args[0].equals("all")) {
            List<IChannel> channels = guild.getChannels();
            for (IChannel channel1 : channels) {
                channel.sendMessage("Scan of " + channel1.getName() + " is running...");
                try {
                    Thread.sleep(1000);  //Sleep to not exceed the rate limit
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.sendMessage(collectMessages(channel1) ? "Scan of " + channel1.getName() + " succesful!" : "Scan " + channel1.getName() + " failed :(");
                try {
                    Thread.sleep(1000);  //Sleep to not exceed the rate limit
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (channelsMentioned.size() == 0) {
            channel.sendMessage("Scan running...");
            channel.sendMessage(collectMessages(channel) ? "Scan successful!" : "Scan failed :(");
        } else {
            for (IChannel aChannelsMentioned : channelsMentioned) {
                channel.sendMessage("Scan of " + aChannelsMentioned.getName() + " is running...");
                channel.sendMessage(collectMessages(aChannelsMentioned) ? "Scan of " + aChannelsMentioned.getName() + " succesful!" : "Scan " + aChannelsMentioned.getName() + " failed :(");
                try {
                    Thread.sleep(1000);  //Sleep to not exceed the rate limit
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
