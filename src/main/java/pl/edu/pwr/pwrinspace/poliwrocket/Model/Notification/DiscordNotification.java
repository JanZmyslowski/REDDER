package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

import java.util.Arrays;

public class DiscordNotification extends Notification {

    private static final Logger logger = LoggerFactory.getLogger(DiscordNotification.class);

    private JDA jda;

    protected String channel = Configuration.getInstance().DISCORD_CHANNEL_NAME;

    public DiscordNotification(NotificationEvent notificationDiscordEvent) {
        super(notificationDiscordEvent);
    }

    public synchronized void setupConnection() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            try {
                jda = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN).build();
                jda.addEventListener(notificationEvent);
            } catch (Exception e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    public synchronized void setupCustom() {
        if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
            JDABuilder builder = JDABuilder.createDefault(Configuration.getInstance().DISCORD_TOKEN);

            // Disable parts of the cache
            builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);

            // Set activity (like "playing Something")
            builder.setActivity(Activity.playing("Flying"));

            try {
                jda = builder.build();
                jda.addEventListener(notificationEvent);
            } catch (Exception e) {
                logger.error(Arrays.toString(e.getStackTrace()));
            }
        }
    }

    protected TextChannel getChannel() {

        if (!channel.equals("")) {
            var channels = jda.getTextChannelsByName(channel, true);
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
            channels = jda.getTextChannels();
            if (!channels.isEmpty()) {
                return channels.get(0);
            }
        }
        return null;
    }

    @Override
    public void sendNotification(String message) {
        getChannel().sendMessage(message).queue();
    }

    @Override
    public void sendNotification(EmbedBuilder message) {
        getChannel().sendMessage(message.build()).queue();
    }

    @Override
    public synchronized void setup() {
        setupCustom();
        if (jda != null) {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                logger.error(Arrays.toString(e.getStackTrace()));
                Thread.currentThread().interrupt();
            }
        }
        super.setup();
    }

    @Override
    public boolean isConnected() {
        return !Configuration.getInstance().DISCORD_TOKEN.equals("") && jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
}
