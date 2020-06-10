/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.lang.reflect.Type;
import java.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * @author Jonas
 *
 */
public class GsonByteArrayToBase64Adapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
	
	@Override
	public byte[] deserialize(JsonElement element, Type type, JsonDeserializationContext ctx)
			throws JsonParseException {
		return Base64.getDecoder().decode(element.getAsString());
	}

	@Override
	public JsonElement serialize(byte[] ba, Type type, JsonSerializationContext ctx) {
		return new JsonPrimitive(Base64.getEncoder().encodeToString(ba));
	}
}
