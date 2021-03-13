package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

public enum ThreadName {
    DISCORD_NOTIFICATION("DiscordNotificationThread"),
    COUNTDOWN("Countdown");

    private String name;

    ThreadName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
