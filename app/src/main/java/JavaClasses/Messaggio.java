package JavaClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class Messaggio {
    private String messaggio;
    private String orario;
    private String nome;
    private String cognome;

    public Messaggio(String messaggio, String orario) throws JSONException {
        System.out.println("JSON top: "+messaggio);
        JSONObject json = new JSONObject(messaggio);
        this.nome = json.getString("nome");
        this.cognome = json.getString("cognome");
        this.messaggio = json.getString("msg");
        this.orario = orario;
    }

    public String getCognome() {
        return cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
