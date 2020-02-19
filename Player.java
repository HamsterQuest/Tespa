public class Player {
	private String role;
	private String bnet;
	private int sr;
	private String overbuff;
	
	public Player(){
		
	}
	public Player(String b){
		bnet = b;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getBnet() {
		return bnet;
	}
	public void setBnet(String bnet) {
		this.bnet = bnet;
	}
	public int getSr() {
		return sr;
	}
	public String getSrString() {
		if(sr == -1){
			return "PP";
		}
		return Integer.toString(sr);
	}
	public void setSr(int sr) {
		this.sr = sr;
	}
	public String getOverbuff() {
		return overbuff;
	}
	public void setOverbuff(String overbuff) {
		this.overbuff = overbuff;
	}
	public String toString() {
		return String.format("%24s ", bnet) + " \t" + getSrString() + "\t" + overbuff + System.getProperty("line.separator");
	}
}
