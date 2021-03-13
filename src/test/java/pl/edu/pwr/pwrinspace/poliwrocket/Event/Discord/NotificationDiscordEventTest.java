package pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class NotificationDiscordEventTest {

    @Mock
    private NotificationFormatService mockNotificationFormatService;

    private NotificationDiscordEvent notificationDiscordEventUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        notificationDiscordEventUnderTest = new NotificationDiscordEvent(mockNotificationFormatService);
    }

    @Test
    void testOnGuildMessageReceived() {
        // Setup
        var mockResponseBuilder = mock(EmbedBuilder.class);
        var mockChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockEvent = mock(GuildMessageReceivedEvent.class);
        var mockAuthor = mock(User.class);
        var mockMessageAction = mock(MessageAction.class);
        var channelName = "NAME";
        var mockGuild = mock(Guild.class);

        when(mockAuthor.isBot()).thenReturn(false);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getAuthor()).thenReturn(mockAuthor);
        Mockito.doNothing().when(mockMessageAction).queue();
        when(mockChannel.sendMessage(any(String.class))).thenReturn(mockMessageAction);
        notificationDiscordEventUnderTest.channelName = channelName;

        var messageKey = "Key";
        when(mockMessage.getContentRaw()).thenReturn(messageKey);
        when(mockEvent.getChannel()).thenReturn(mockChannel);
        when(mockChannel.getName()).thenReturn(channelName);
        when(mockEvent.getGuild()).thenReturn(mockGuild);
        when(mockGuild.getTextChannelsByName(channelName,true)).thenReturn(new ArrayList<TextChannel>());

        PowerMockito.when(notificationDiscordEventUnderTest.getChannel(mockEvent)).thenReturn(mockChannel);
        when(mockNotificationFormatService.getFormattedMessage(messageKey)).thenReturn("Result");


        // Run the test
        notificationDiscordEventUnderTest.onGuildMessageReceived(mockEvent);

        // Verify the results
        verify(mockNotificationFormatService,times(1)).getFormattedMessage(messageKey);
        verify(mockChannel,times(1)).sendMessage("Result");
        verify(mockMessageAction,times(1)).queue();
    }


    @Test
    void testOnGuildMessageReceivedError() throws Exception {
        // Setup
        var mockResponseBuilder = mock(EmbedBuilder.class);
        var mockChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockEvent = mock(GuildMessageReceivedEvent.class);
        var mockAuthor = mock(User.class);
        var mockMessageAction = mock(MessageAction.class);
        var channelName = "NAME";
        var mockGuild = mock(Guild.class);

        when(mockAuthor.isBot()).thenReturn(false);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getAuthor()).thenReturn(mockAuthor);
        Mockito.doNothing().when(mockMessageAction).queue();
        when(mockChannel.sendMessage(any(String.class))).thenReturn(mockMessageAction);
        notificationDiscordEventUnderTest.channelName = channelName;

        var messageKey = "Error";
        when(mockMessage.getContentRaw()).thenReturn(messageKey);
        when(mockEvent.getChannel()).thenReturn(mockChannel);
        when(mockChannel.getName()).thenReturn(channelName);
        when(mockEvent.getGuild()).thenReturn(mockGuild);
        when(mockGuild.getTextChannelsByName(channelName,true)).thenReturn(new ArrayList<TextChannel>());
        PowerMockito.when(notificationDiscordEventUnderTest.getChannel(mockEvent)).thenReturn(mockChannel);
        when(mockNotificationFormatService.getFormattedMessage(messageKey)).thenReturn("Error - Result");


        // Run the test
        notificationDiscordEventUnderTest.onGuildMessageReceived(mockEvent);

        // Verify the results
        verify(mockNotificationFormatService,times(1)).getFormattedMessage(messageKey);
        verify(mockChannel,times(1)).sendMessage("Error - Result");
        verify(mockMessageAction,times(1)).queue();
    }

    @Test
    void testOnGuildMessageReceivedErrorDifferentChannel() throws Exception {
        // Setup
        var mockResponseBuilder = mock(EmbedBuilder.class);
        var mockChannel = mock(TextChannel.class);
        var mockOtherChannel = mock(TextChannel.class);
        var mockMessage = mock(Message.class);
        var mockEvent = mock(GuildMessageReceivedEvent.class);
        var mockAuthor = mock(User.class);
        var mockMessageAction = mock(MessageAction.class);
        var channelName = "NAME";
        var mockGuild = mock(Guild.class);

        when(mockAuthor.isBot()).thenReturn(false);
        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getAuthor()).thenReturn(mockAuthor);
        Mockito.doNothing().when(mockMessageAction).queue();
        when(mockChannel.sendMessage(any(String.class))).thenReturn(mockMessageAction);
        notificationDiscordEventUnderTest.channelName = channelName;

        var messageKey = "Error";
        when(mockMessage.getContentRaw()).thenReturn(messageKey);
        when(mockEvent.getChannel()).thenReturn(mockChannel);
        when(mockChannel.getName()).thenReturn(channelName);
        when(mockOtherChannel.getName()).thenReturn(channelName+"xyz");
        when(mockEvent.getGuild()).thenReturn(mockGuild);

        PowerMockito.when(notificationDiscordEventUnderTest.getChannel(mockEvent)).thenReturn(mockOtherChannel);
        when(mockNotificationFormatService.getFormattedMessage(messageKey)).thenReturn("Error - Result");


        // Run the test
        notificationDiscordEventUnderTest.onGuildMessageReceived(mockEvent);

        // Verify the results
        verify(mockNotificationFormatService,times(1)).getFormattedMessage(messageKey);
        verify(mockChannel,never()).sendMessage("Error - Result");
        verify(mockMessageAction,never()).queue();
    }

}
