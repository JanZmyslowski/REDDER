# RocketEngineDynoDataExternalRecorder

REDDER is the application for duplex communication with rocket engine dyno.

## Installation
Requires Java 11, JavaFX 13, and Maven.

## Usage

Adjust configuration in config.json file.

To use Discord bot specify discord token in config.json:
```bash
"DISCORD_TOKEN": "$YOUR_DISCORD_TOKEN",
"DISCORD_CHANNEL_NAME": "$YOUR_DISCORD_CHANNEL",
```

All received data are store in ```.\flightData```.
## Deploy

To deploy the application use:
```bash
mvn package