import java.io.Serializable;

public class Message  implements Serializable{
	private static final long serialVersionUID = 7368724605124214940L;//generat aleator
	private final MessageType type; //retine tipul mesajului
	private final Object[] args; //retine continutul mesajului

	public Message(MessageType type, Object[] args){
		this.type = type;
		this.args = args;

	}
//returneaza tipul mesajului
	public MessageType getType() {
		return type;
	}
//returneaza continutul mesajului
	public Object[] getArgs() {
		return args; 
	}
//enumeratie ce retine tipurile de mesaje (transfer date si mesaj scrise)
	public enum MessageType {
		DATA_TRANSFER, MSG

	}
}
