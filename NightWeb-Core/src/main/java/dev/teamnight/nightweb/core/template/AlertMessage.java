package dev.teamnight.nightweb.core.template;

public class AlertMessage {

	private Type type = Type.STANDARD;
	private String message;
	
	public AlertMessage(String message) {
		this.message = message;
	}
	
	public AlertMessage(String message, Type type) {
		this(message);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public enum Type {
		STANDARD("primary"),
		SUCCESS("success"),
		ERROR("danger"),
		WARNING("warning"),
		INFO("primary");
		
		String htmlClass;
		
		private Type(String htmlClass) {
			this.htmlClass = htmlClass;
		}
		
		public String asString() {
			return this.htmlClass;
		}
	}
}