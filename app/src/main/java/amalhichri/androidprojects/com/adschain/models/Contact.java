package amalhichri.androidprojects.com.adschain.models;



public class Contact {

    private String id;
    private String nom;
    private String num;
   // private boolean etat;

    /*public Contact(String nom, String num, boolean etat) {
        this.nom = nom;
        this.num = num;
      this.etat = etat;
    }*/

    public Contact() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

   /* public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }*/


    @Override
    public String toString() {
        return  num ;
    }
}
