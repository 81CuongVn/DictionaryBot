package commands;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.OutputStream;
import java.util.Dictionary;

/**
 * Created by thewithz on 6/16/16.
 */
public class DiscordAsOutputStream extends OutputStream {

	private StringBuilder anotatedText;
	private StringBuilder finalOutput;
	private TextChannel outChannel;

	public DiscordAsOutputStream(TextChannel outChannel) {
		this.outChannel = outChannel;
		anotatedText = new StringBuilder();
		finalOutput = new StringBuilder();
	}

	@Override
	public void write(int b) {
		if(b == '\n') {
			anotatedText.append((char) b);
			finalOutput.append(anotatedText.toString());
			anotatedText = new StringBuilder();
			return;
		}
		anotatedText.append((char) b);
	}

	public void myPrint() {
		if(finalOutput.length() > 0) {
			if(finalOutput.length() <= 2000)
				outChannel.sendMessage(new MessageBuilder().appendCodeBlock(finalOutput.toString(), "java")
				                                           .build())
				          .queue();
			else {
				outChannel.sendMessage("Error too big")
				          .queue();
				System.err.println(finalOutput);
			}
		}
		finalOutput = new StringBuilder();
	}
}
