package JavaClasses;

public class Utente {
    private String nome;
    private String cognome;
    private String gruppo;
    private String cabina;

    public Utente(String nome, String cognome, String gruppo, String cabina){
        this.nome = nome;
        this.cognome = cognome;
        this.gruppo = gruppo;
        this.cabina = cabina;
    }

    public String getNome() {
        return nome;
    }
    public String getCognome(){
        return cognome;
    }
    public String getGruppo(){
        return gruppo;
    }
    public String getCabina(){
        return cabina;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setCognome(String cognome){
        this.cognome = cognome;
    }
    public void setGruppo(String gruppo){
        this.gruppo = gruppo;
    }
    public void setCabina(String cabina){
        this.cabina = cabina;
    }
}
