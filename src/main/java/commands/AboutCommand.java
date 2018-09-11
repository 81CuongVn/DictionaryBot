package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.bot.entities.ApplicationInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class AboutCommand extends Command {
	private final Color color;
	private final Permission[] perms;
	private String oauthLink;
	private final String[] features;

	public AboutCommand(Color color, String[] features, Permission... perms) {
		this.color = color;
		this.features = features;
		this.name = "about";
		this.help = "shows info about the bot";
		this.guildOnly = false;
		this.perms = perms;
		this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected void execute(CommandEvent event) {
		if (oauthLink == null) {
			try {
				ApplicationInfo info = event.getJDA()
				                            .asBot()
				                            .getApplicationInfo()
				                            .complete();
				oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
			} catch (Exception e) {
				Logger log = LoggerFactory.getLogger("OAuth2");
				log.error("Could not generate invite link ", e);
				oauthLink = "";
			}
		}
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(event.getGuild() == null
		                 ? color
		                 : event.getGuild()
		                        .getSelfMember()
		                        .getColor());
		builder.setAuthor("All about this bot!",
		                  null,
		                  event.getSelfUser()
		                       .getAvatarUrl());
		String author = event.getJDA()
		                     .getUserById(event.getClient()
		                                       .getOwnerId()) == null
		                ? "<@" + event.getClient()
		                              .getOwnerId() + ">"
		                : event.getJDA()
		                       .getUserById(event.getClient()
		                                         .getOwnerId())
		                       .getName();
		StringBuilder descr = new StringBuilder().append("Hello! I am the Dictionary Bot**, ")
		                                         .append("\nI was written in Java by**")
		                                         .append(author)
		                                         .append("\nType `")
		                                         .append(event.getClient()
		                                                      .getTextualPrefix())
		                                         .append(event.getClient()
		                                                      .getHelpWord())
		                                         .append("` to see my commands!")
		                                         .append("\n\nSome of my features include: ```css");
		String REPLACEMENT_ICON = "+";
		for (String feature : features)
			descr.append("\n")
			     .append(REPLACEMENT_ICON)
			     .append(" ")
			     .append(feature);
		descr.append(" ```");
		builder.setDescription(descr);
		if (event.getJDA()
		         .getShardInfo() == null) {
			builder.addField("Stats",
			                 event.getJDA()
			                      .getGuilds()
			                      .size() + " servers\n1 shard",
			                 true);
			builder.addField("Users",
			                 event.getJDA()
			                      .getUsers()
			                      .size() + " unique\n" + event.getJDA()
			                                                   .getGuilds()
			                                                   .stream()
			                                                   .mapToInt(g -> g.getMembers()
			                                                                   .size())
			                                                   .sum() + " total",
			                 true);
			builder.addField("Channels",
			                 event.getJDA()
			                      .getTextChannels()
			                      .size() + " Text\n" + event.getJDA()
			                                                 .getVoiceChannels()
			                                                 .size() + " Voice",
			                 true);
		} else {
			builder.addField("Stats",
			                 (event.getClient()).getTotalGuilds() + " Servers\nShard " + (event.getJDA()
			                                                                                   .getShardInfo()
			                                                                                   .getShardId() + 1) + "/" + event.getJDA()
			                                                                                                                   .getShardInfo()
			                                                                                                                   .getShardTotal(),
			                 true);
			builder.addField("This shard",
			                 event.getJDA()
			                      .getUsers()
			                      .size() + " Users\n" + event.getJDA()
			                                                  .getGuilds()
			                                                  .size() + " Servers",
			                 true);
			builder.addField("",
			                 event.getJDA()
			                      .getTextChannels()
			                      .size() + " Text Channels\n" + event.getJDA()
			                                                          .getVoiceChannels()
			                                                          .size() + " Voice Channels",
			                 true);
		}
		builder.setFooter("Last restart", null);
		builder.setTimestamp(event.getClient()
		                          .getStartTime());
		event.reply(builder.build());
	}

}