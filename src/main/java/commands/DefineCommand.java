package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.Permission;
import tech.thewithz.oxforddict.OxfordDictionary;

import java.awt.*;
import java.util.Arrays;

public class DefineCommand extends Command {
	private OxfordDictionary dict;
	private EventWaiter waiter;

	public DefineCommand(OxfordDictionary dict, EventWaiter waiter) {
		this.name = "define";
		this.help = "Defines a word\nUsage: !define <word>\n";
		this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
		this.guildOnly = false;
		this.dict = dict;
		this.waiter = waiter;
	}

	@Override
	protected void execute(CommandEvent event) {
		String[] args = event.getArgs()
		                     .split("\\s+");
		System.out.println(Arrays.toString(args) + "\n" + args[0]);
		System.out.println(dict.entries(args[0])
		                       .build()
		                       .toString());
		Paginator.Builder builder = new Paginator.Builder();
		// for each definition, populate a page in the paginator.
		builder.setColor(Color.GREEN)
		       .setItemsPerPage(1)
		       .setText("Definition of " + args[0])
		       .setEventWaiter(waiter)
		       .wrapPageEnds(true)
		       .setUsers(event.getAuthor())
		       .addItems(args)
		       .build();
		builder.build()
		       .display(event.getChannel());
	}

}