package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.thewithz.oxforddict.OxfordDictionary;
import tech.thewithz.oxforddict.exceptions.WordNotFoundException;

import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;

public class SynonymCommand extends Command {
	private OxfordDictionary dict;
	private EventWaiter waiter;

	public SynonymCommand(OxfordDictionary dict, EventWaiter waiter) {
		this.name = "synonym";
		this.help = "Returns synonyms for a word\nUsage: !syn <word>\n";
		this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
		this.guildOnly = false;
		this.dict = dict;
		this.waiter = waiter;
	}

	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs()
		                     .split("\\s+");
		JSONObject response = getResponse(args, new DiscordAsOutputStream(event.getJDA().getTextChannelById(129721816972394497L)), event.getTextChannel());
		if (response == null) {
			return;
		}
		ArrayList<String> synonyms = getSynonyms(response);
		Paginator.Builder builder = new Paginator.Builder();
		// for each definition, populate a page in the paginator.
		System.out.println(response.toString());
		System.out.println(Arrays.toString(synonyms.toArray(new String[0])));
		// TODO: 1/23/19 fix automatic deletion of single definition paginators
		builder.setColor(Color.GREEN)
		       .setItemsPerPage(1)
		       .setText("Definition of " + args[0])
		       .setEventWaiter(waiter)
		       .wrapPageEnds(true)
		       .setUsers(event.getAuthor())
		       .addItems(synonyms.toArray(new String[0]))
		       .build()
		       .display(event.getChannel());
	}

	private JSONObject getResponse(String[] args, DiscordAsOutputStream discordAsOutputStream, TextChannel channel) {
		JSONObject response = null;
		try {
			response = dict.thesaurus(args[0], false, true);
		}catch (WordNotFoundException e) {
			channel.sendMessage(args[0] + " has no synonyms.").queue();
		}
		catch (RuntimeException e) {
			e.printStackTrace(new PrintStream(discordAsOutputStream));
		}finally {
			discordAsOutputStream.myPrint();
		}
		return response;
	}

	private ArrayList<String> getSynonyms(JSONObject response) {
		ArrayList<String> synonyms = new ArrayList<>();
		JSONArray results = response.getJSONArray("results");
		// TODO: 1/24/19 actually write this method
		return synonyms;
	}

}