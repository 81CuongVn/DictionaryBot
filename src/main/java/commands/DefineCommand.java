package commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.Permission;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.thewithz.oxforddict.OxfordDictionary;

import java.awt.*;
import java.util.ArrayList;
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
		JSONObject response = dict.entries(args[0])
		                          .build();
		ArrayList<String> definitions = getDefinitions(response);
		Paginator.Builder builder = new Paginator.Builder();
		// for each definition, populate a page in the paginator.
		System.out.println(Arrays.toString(definitions.toArray(new String[0])));
		// TODO: 1/23/19 fix automatic deletion of single definition paginators
		builder.setColor(Color.GREEN)
		       .setItemsPerPage(1)
		       .setText("Definition of " + args[0])
		       .setEventWaiter(waiter)
		       .wrapPageEnds(true)
		       .setUsers(event.getAuthor())
		       .addItems(definitions.toArray(new String[0]))
		       .build()
		       .display(event.getChannel());
	}

//	private String[] getDefinitions(JSONObject definitions) {
//		ArrayList<String> list = new ArrayList<>();
//		definitions.getJSONArray("results")
//		           .getJSONObject(0)
//		           .getJSONArray("lexicalEntries")
//		           .forEach(index1 -> {
//			           JSONObject obj1 = (JSONObject) index1;
//			           obj1.getJSONArray("entries")
//			               .forEach(index2 -> {
//				               JSONObject obj2 = (JSONObject) index2;
//				               obj2.getJSONArray("senses")
//				                   .forEach(index3 -> {
//					                   JSONObject obj3 = (JSONObject) index3;
//					                   if(obj3.has("definitions")) {
//						                   JSONArray arr = obj3.getJSONArray("definitions");
//						                   int arraySize = arr.length();
//						                   for(int i = 0; i < arraySize; i++) {
//							                   list.add(arr.getString(i));
//						                   }
//					                   }
//				                   });
//			               });
//		           });
//		String[] ar = new String[list.size()];
//		return list.toArray(ar);
//	}

	private ArrayList<String> getDefinitions(JSONObject response) {
		ArrayList<String> definitions = new ArrayList<>();
		JSONArray results = response.getJSONArray("results");
		for(Object obj1 : results) {
			JSONArray lexicalEntries = ((JSONObject) obj1).getJSONArray("lexicalEntries");
			for(Object obj2 : lexicalEntries) {
				JSONArray entries = ((JSONObject) obj2).getJSONArray("entries");
				for(Object obj3 : entries) {
					JSONArray senses = ((JSONObject) obj3).getJSONArray("senses");
					for(Object obj4 : senses) {
						if(((JSONObject) obj4).has("definitions"))
							((JSONObject) obj4).getJSONArray("definitions")
							                   .forEach((str) -> definitions.add((String) str));
					}
				}
			}
		}
		return definitions;
	}

}