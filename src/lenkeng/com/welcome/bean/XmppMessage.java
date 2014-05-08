package lenkeng.com.welcome.bean;

/*
 * $Id: XmppMessage.java 26 2014-01-02 01:41:04Z gww $
 */
public class XmppMessage {
	private String style;
	private String content;
	private String variable;
	private String msgTime;
	private int isRead;
	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public int isRead() {
		return isRead;
	}

	public void setRead(int isRead) {
		this.isRead = isRead;
	}

	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	private int msgId;

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

}
