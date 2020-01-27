import java.io.Serializable;

public class Message implements Serializable
{
	private String msg = null;

	//getters
	public String getMsg(){
		return this.msg;
	}

	//setters
	public void setMsg(String m){
		this.msg = m;
	}

	// constructor
	public Message()
	{
	}

}
