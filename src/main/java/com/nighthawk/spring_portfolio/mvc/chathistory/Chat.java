package com.nighthawk.spring_portfolio.mvc.chathistory;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.json.simple.JSONObject;

@Entity
public class Chat {
	 // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    public Long getId() {
		return id;
	}
	private String chatMessage;
    private String chatReponse;
    private Date timestamp;
    private Long personId;
    
	public Long getPersonId() {
		return personId;
	}
	public String getChatMessage() {
		return chatMessage;
	}
	public String getChatReponse() {
		return chatReponse;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public Chat(String chatMessage, String chatReponse, Date timestamp, Long personId) {
		super();
		this.chatMessage = chatMessage;
		this.chatReponse = chatReponse;
		this.timestamp = timestamp;
		this.personId = personId;
	}
    
	public Chat() {
		
	}
    // todo add person id foreign key
	
	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put(idStr, id);
		obj.put(chat_message, chatMessage);
		obj.put(chat_response, chatReponse);
		obj.put(timestamp_str, timestamp.toString());
		obj.put(person_id, personId);
		
		return obj.toString();
	}
	
	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put(idStr, id);
		obj.put(chat_message, chatMessage);
		obj.put(chat_response, chatReponse);
		obj.put(timestamp_str, timestamp.toString());
		obj.put(person_id, personId);
		
		return obj;
	}
	
	private static final String chat_message="chat_message";
	private static final String idStr="id";
	private static final String chat_response="chat_response";
	private static final String timestamp_str="timestamp";
	private static final String person_id="person_id";
	

}
