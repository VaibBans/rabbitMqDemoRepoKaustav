package com.example.demo;

//import java.util.UUID;



public class Messages {



    private int id;


    private String message;



    Messages() {

    }



    public Messages(int id,String message) {

        this.id = id;
        this.message = message;

    }



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	@Override
	public String toString() {
		return "Book [id=" + id + ", message=" + message + "]";
	}


}