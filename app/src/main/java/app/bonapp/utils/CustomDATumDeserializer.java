package app.bonapp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import app.bonapp.models.merchantposts.DATum;


public class CustomDATumDeserializer implements JsonDeserializer<DATum> {

    @Override
    public DATum deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DATum datum = new Gson().fromJson(json, DATum.class);
        String isFavorite = json.getAsJsonObject().get("is_favorite").getAsString();
        JsonElement allowedDelivery = json.getAsJsonObject().get("allow_delivery");
        if (isFavorite == null || !isFavorite.equals("1")) {
            datum.setFavorite(false);
        } else {
            datum.setFavorite(true);
        }

        if (allowedDelivery.isJsonNull() || !allowedDelivery.getAsString().equals("1")) {
            datum.setAllowDelivery(false);
        } else {
            datum.setAllowDelivery(true);
        }

        return datum;
    }


}
