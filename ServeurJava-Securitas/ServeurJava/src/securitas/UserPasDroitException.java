package securitas;

public class UserPasDroitException extends Exception{
	private static final long serialVersionUID = 1L;
	public String user;
	public UserPasDroitException(String user) {
		this.user=user;
	}
	
	public String toString() {
		return "L'utilisateur : " + this.user + " n'a pas le droit de dévéroullier la serrure.";
	}
}
