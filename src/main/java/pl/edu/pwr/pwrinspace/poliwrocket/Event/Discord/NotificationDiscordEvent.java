package pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;

public class NotificationDiscordEvent extends NotificationEvent {

    protected String channelName = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    public NotificationDiscordEvent(NotificationFormatService notificationFormatService) {
        super(notificationFormatService);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if(!event.getAuthor().isBot()) {
            String messageReceived = event.getMessage().getContentRaw();
            var message = notification.getFormattedMessage(messageReceived);
            if(message instanceof EmbedBuilder){
                this.getChannel(event).sendMessage(((EmbedBuilder) message).build()).queue();
            } else if (message instanceof String){
                TextChannel channel = this.getChannel(event);
                if(!((String) message).contains("Error") || event.getChannel().getName().equals(channelName)){
                    channel.sendMessage(((String) message)).queue();

                }
            }
        }
    }

    protected TextChannel getChannel(@NotNull GuildMessageReceivedEvent event) {

        if (!channelName.equals("")) {
            var channels = event.getGuild().getTextChannelsByName(channelName, true);
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
        }
        return event.getChannel();
    }
}
