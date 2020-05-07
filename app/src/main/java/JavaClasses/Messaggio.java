package JavaClasses;

public class Messaggio {
    private String messaggio;
    private String orario;

    public Messaggio(String messaggio, String orario){
        this.messaggio = messaggio;
        this.orario = orario;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    @Override
    public String toString() {
        return "Messaggio{" +
                "messaggio='" + messaggio + '\'' +
                ", orario='" + orario + '\'' +
                '}';
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
