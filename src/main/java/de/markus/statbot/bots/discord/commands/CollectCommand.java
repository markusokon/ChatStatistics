package de.markus.statbot.bots.discord.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.markus.statbot.model.Channel;
import de.markus.statbot.model.Server;
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
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

@Slf4j
@Component
public class CollectCommand implements CommandExecutor {

    private final transient MessageRepository messageRepository;
    private final transient ChannelRepository channelRepository;
    private final transient UserRepository userRepository;
    private final transient ServerRepository serverRepository;

    @Autowired
    public CollectCommand(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository, ServerRepository serverRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
    }


    @Command(aliases = "!collect")
    public void onCommand(IGuild guild, IChannel channel, IMessage message) {
        if (message.getAuthor().getLongID() != 263019299751460864L/*Wormi#1337s ID*/) {
            channel.sendMessage("You are not " + guild.getUserByID(263019299751460864L) + "!");
            return;
        }
        channel.sendMessage("Collecting infrastructuredata..");
        channel.sendMessage(collectGuildData(guild) ? "Infrastructuredata collection successful." : "Infrastructuredata collection failed.");
    }

    private boolean collectGuildData(IGuild guild) {
        try {
            //Add Guild to database if it doesn't exist already
            serverRepository.deleteById(guild.getLongID());
            Server server = new Server(guild.getLongID(), guild.getName(), null);
            serverRepository.saveAndFlush(server);
            server.setUsers(userRepository.findAllByServers(server));
            serverRepository.saveAndFlush(server);

            //Get users in guild and add them to the database if they don't already exist
            List<IUser> users = guild.getUsers();
            for (IUser iUser : users) {
                userRepository.deleteById(iUser.getLongID());
                User user = new User(iUser.getLongID(), iUser.getName());
                userRepository.saveAndFlush(user);
            }

            //Get channels in guild and add them to the database if they don't already exist
            List<IChannel> channels = guild.getChannels();
            for (IChannel aChannel : channels) {
                channelRepository.deleteById(aChannel.getLongID());
                Channel bChannel = new Channel(aChannel.getLongID(), aChannel.getName(), serverRepository.getOne(guild.getLongID()));
                channelRepository.saveAndFlush(bChannel);
            }
            return true;
        } catch (Exception e) {
            log.error("Exception in collectGuildData was thrown!");
            e.printStackTrace();
            return false;
        }
    }
}
