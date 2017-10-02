package com.ehsure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {

	public static byte[] serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);) {
			oos.writeObject(obj);
			byte[] bytes = bos.toByteArray();
			return bytes;
		}
	}

	public static <T> T unserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bis);) {
			return clazz.cast(ois.readObject());
		}
	}
}
