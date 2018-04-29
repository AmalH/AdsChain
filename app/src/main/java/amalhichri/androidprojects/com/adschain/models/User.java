package amalhichri.androidprojects.com.adschain.models;

/**
 * Created by Amal on 14/11/2017.
 */

public class User {

    private String id,emailAddress,pictureUrl,firstName,lastName,twoFactorAuthOn,phoneNumber;  //fields to match with LinkedIn API
                                           // for facebook API we just read the json response object without switching it to a User object

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTwoFactorAuthOn() {
        return twoFactorAuthOn;
    }

    public void setTwoFactorAuthOn(String twoFactorAuthOn) {
        this.twoFactorAuthOn = twoFactorAuthOn;
    }

    // just for test

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
