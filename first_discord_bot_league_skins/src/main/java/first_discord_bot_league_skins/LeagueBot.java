package first_discord_bot_league_skins;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LeagueBot extends ListenerAdapter {
	private final String discordChannelID = "YOUR_CHANNEL_ID_HERE";
	private final String youtubeChannelID = "THE_YOUTUBERS_CHANNEL_ID_HERE";
	private String lastVideoId = "";
	//main method that takes the programmers discord bot made from discord and allows the user to program it
	public static void main(String[] args) throws LoginException, InterruptedException{
		
		JDA jda = JDABuilder.createDefault("YOUR_DISCORD_BOT_TOKEN_HERE")
				.enableIntents(GatewayIntent.MESSAGE_CONTENT)
				.addEventListeners(new LeagueBot())
				.build();
	}
	//method that lets the programmer know that the bot is ready to test
	public void onReady(ReadyEvent event) {
		
		System.out.println("The bot is ready!");
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(() -> checkYoutubeFeed(event.getJDA()), 0, 5, TimeUnit.MINUTES);
	}
	//method that reacts to users messages
	public void checkYoutubeFeed(JDA jda) {
		String feedUrl = "https://www.youtube.com/feeds/videos.xml?channel_id=" + youtubeChannelID;
		try {
            Document doc = Jsoup.connect(feedUrl).get();
            Element latest = doc.selectFirst("entry");
            if (latest == null) return;

            String videoId = latest.getElementsByTag("yt:videoId").first().text();
            String title = latest.getElementsByTag("title").first().text();

            if (!videoId.equals(lastVideoId)) {
                lastVideoId = videoId;
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
                String message = " **New video uploaded!**\n" + title + "\n" + videoUrl;

                jda.getTextChannelById(discordChannelID)
                        .sendMessage(message)
                        .queue();

                System.out.println("Posted new video: " + title);
            }

        } catch (IOException e) {
            System.err.println("Error fetching YouTube feed: " + e.getMessage());
        }
    }
	
}
