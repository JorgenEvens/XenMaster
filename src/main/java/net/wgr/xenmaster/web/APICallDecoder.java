/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.wgr.xenmaster.web.Hook.APICall;

/**
 * 
 * @created Nov 7, 2011
 * @author double-u
 */
public class APICallDecoder implements JsonDeserializer<Hook.APICall> {

    @Override
    public APICall deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new IllegalArgumentException("Invalid JSON Apic object");
        }
        JsonObject obj = (JsonObject) json;
        APICall apic = new APICall();
        for (Entry<String, JsonElement> e : obj.entrySet()) {
            switch (e.getKey()) {
                case "ref":
                    if (e.getValue() != null && !(e.getValue() instanceof JsonNull)) {
                        apic.ref = e.getValue().getAsString();
                    }
                    break;
                case "args":
                    if (e.getValue().isJsonArray()) {
                        JsonArray arr = e.getValue().getAsJsonArray();
                        ArrayList<Object> args = new ArrayList<>(arr.size());
                        for (int i = 0; i < arr.size(); i++) {
                            JsonElement value = arr.get(i);
                            if (value.isJsonPrimitive()) {
                                args.add(value.getAsString());
                            } else if (value.isJsonObject()) {
                                args.add(deserializeToMap(value.getAsJsonObject()));
                            }
                        }
                        apic.args = args.toArray();
                    } else {
                        apic.args = null;
                    }
                    break;
            }
        }
        return apic;
    }

    protected Map<String, Object> deserializeToMap(JsonObject obj) {
        HashMap<String, Object> map = new HashMap<>();
        for (Entry<String, JsonElement> e : obj.entrySet()) {
            if (e.getValue().isJsonPrimitive()) {
                map.put(e.getKey(), e.getValue().getAsString());
            } else if (e.getValue().isJsonObject()) {
                map.put(e.getKey(), deserializeToMap(e.getValue().getAsJsonObject()));
            }
        }
        return map;
    }
}
