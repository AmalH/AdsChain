package amalhichri.androidprojects.com.adschain.models;


public class Deal {

    private String id;
    private String userId;
    private int nbSMS;
    private int nbSMSleft;


    public Deal(String userId, int nbSMS, int nbSMSleft) {
        this.userId = userId;
        this.nbSMS = nbSMS;
        this.nbSMSleft = nbSMSleft;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNbSMS() {
        return nbSMS;
    }

    public void setNbSMS(int nbSMS) {
        this.nbSMS = nbSMS;
    }

    public int getNbSMSleft() {
        return nbSMSleft;
    }

    public void setNbSMSleft(int nbSMSleft) {
        this.nbSMSleft = nbSMSleft;
    }
}
