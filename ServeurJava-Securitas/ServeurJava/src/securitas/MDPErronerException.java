package securitas;

public class MDPErronerException extends Exception{
	private static final long serialVersionUID = 1L;
	public String mdp;
	public MDPErronerException(String mdp) {
		this.mdp=mdp;
	}
	
	public String toString() {
		return "le mot de passe : " + this.mdp + "n'est pas correcte.";
	}
}