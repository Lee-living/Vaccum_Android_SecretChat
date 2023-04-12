/*
package com.lee.domain;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Base64;

public class MessageAdapter extends TypeAdapter<Message> {

    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, Message message) throws IOException {
        throw new UnsupportedOperationException();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Message read(JsonReader in) throws IOException {
        Message message = new Message();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "sign":
                    message.setSign(in.nextInt());
                    break;
                case "user":
                    message.setUser(in.nextString());
                    break;
                case "msg":
                    message.setMsg(in.nextString());
                    break;
                case "secretMsg":
                    String secretMsg = in.nextString();
                    message.setSecretMsg(Base64.getDecoder().decode(secretMsg));
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return message;
    }
}
*/
