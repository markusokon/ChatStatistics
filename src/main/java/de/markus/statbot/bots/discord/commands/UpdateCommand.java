package de.markus.statbot.bots.discord.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.markus.statbot.repositories.ChannelRepository;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.ServerRepository;
import de.markus.statbot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

public class UpdateCommand implements CommandExecutor {

    private final transient MessageRepository messageRepository;
    private final transient ChannelRepository channelRepository;
    private final transient UserRepository userRepository;
    private final transient ServerRepository serverRepository;

    @Autowired
    public UpdateCommand(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository, ServerRepository serverRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.serverRepository = serverRepository;
    }

    @Command(aliases = "!update")
    public void onCommand(IChannel channel, IGuild iGuild, String[] args) {

        channel.sendMessage("test");
    }
}
