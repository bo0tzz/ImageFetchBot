# ImageFetchBot
A telegram bot to fetch images from Google

This bot lets you use Telegram's inline bot functionality to search Google images and send them in a chat!
You can find a working version of the bot on Telegram at [@ImageFetcherBot](http://t.me/imagefetcherbot),
or compile it yourself with `mvn clean package`

There is also a [Docker image](https://hub.docker.com/r/bo0tzz/imagefetchbot/). When running this way, add the telegram API key as the environment variable `BOT_KEY` and mount your google API keys file onto `/app/run/keys/key`.

Makes use of the [jTelegramBotAPI](https://github.com/jTelegram/jTelegramBotAPI).
