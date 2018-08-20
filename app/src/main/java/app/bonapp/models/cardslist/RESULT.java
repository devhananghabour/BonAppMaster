
package app.bonapp.models.cardslist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RESULT {

    @SerializedName("card_id")
    @Expose
    private String cardId;
    @SerializedName("card_name")
    @Expose
    private String cardName;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("card_token")
    @Expose
    private String cardToken;
    @SerializedName("email")
    @Expose
    private String tokenCustomerEmail;
    @SerializedName("password")
    @Expose
    private String tokenCustomerPassword;


    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getTokenCustomerEmail() {
        return tokenCustomerEmail;
    }

    public void setTokenCustomerEmail(String tokenCustomerEmail) {
        this.tokenCustomerEmail = tokenCustomerEmail;
    }

    public String getTokenCustomerPassword() {
        return tokenCustomerPassword;
    }

    public void setTokenCustomerPassword(String tokenCustomerPassword) {
        this.tokenCustomerPassword = tokenCustomerPassword;
    }
}
