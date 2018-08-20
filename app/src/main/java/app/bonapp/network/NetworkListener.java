package app.bonapp.network;

/**
 * It provides an interface between Classes hitting Service and NetworkConnection Class
 */

public interface NetworkListener {

    /**
     * This method is called when response ir received with code 200
     *
     * @param responseCode- code of response
     * @param response-     response in String
     */
    void onSuccess(int responseCode, String response);


    /**
     * This method is called when response is received in the error body
     *
     * @param response - response in the error body
     */
    void onSuccessErrorBody(String response);


    /**
     * This method is called when we receive a call in failure
     */
    void onFailure();


}


