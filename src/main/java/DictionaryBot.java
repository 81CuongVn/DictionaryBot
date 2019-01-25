import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import commands.AboutCommand;
import commands.DefineCommand;
import commands.DiscordAsOutputStream;
import commands.SynonymCommand;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONObject;
import tech.thewithz.oxforddict.OxfordDictionary;
import tech.thewithz.oxforddict.OxfordDictionaryBuilder;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class DictionaryBot {

	public static JDA jda;

	public static void main(String[] args) throws IOException, LoginException, IllegalArgumentException {
		JSONObject obj = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));

		OxfordDictionary dict = new OxfordDictionaryBuilder().setCredentials(obj.getString("app_key"),
		                                                                     obj.getString("app_id"))
		                                                     .build();

		EventWaiter waiter = new EventWaiter();

		CommandClientBuilder client = new CommandClientBuilder();

		client.useDefaultGame();

		client.setOwnerId("122764399961309184");

		client.setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26");

		client.setPrefix(">");

		Command[] commands = {new AboutCommand(Color.BLUE,
		                                       new String[]{"Defines a word}"},
		                                       Permission.ADMINISTRATOR), new DefineCommand(dict,
		                                                                                    waiter), new SynonymCommand(
				dict,
				waiter)};

		client.setHelpConsumer((event) -> {
			StringBuilder builder = new StringBuilder("**commands:**\n");
			Command.Category category = null;
			for(Command command : commands) {
				if(!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
					if(!Objects.equals(category, command.getCategory())) {
						category = command.getCategory();
						builder.append("\n\n  __")
						       .append(category == null ? "No Category" : category.getName())
						       .append("__:\n");
					}
					builder.append("\n`")
					       .append(">")
					       .append(command.getName())
					       .append(command.getArguments() == null ? "`" : " " + command.getArguments() + "`")
					       .append(" - ")
					       .append(command.getHelp());
				}
			}
			User owner = event.getJDA()
			                  .getUserById("122764399961309184");
			if(owner != null) {
				builder.append("\n\nFor additional help, contact **")
				       .append(owner.getName())
				       .append("**#")
				       .append(owner.getDiscriminator());
			}
			event.replyInDm(builder.toString(), unused -> {
			}, t -> event.replyWarning("Help cannot be sent because you are blocking Direct Messages."));
		});

		client.addCommands(commands);
		jda = new JDABuilder(AccountType.BOT).setToken(obj.getString("token"))
		                               .setStatus(OnlineStatus.DO_NOT_DISTURB)
		                               .setGame(Game.playing("loading..."))
		                               .addEventListener(waiter)
		                               .addEventListener(client.build())
		                               .buildAsync();
	}
}
