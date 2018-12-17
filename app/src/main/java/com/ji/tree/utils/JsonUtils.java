package com.ji.tree.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class JsonUtils {
    @Target(ElementType.FIELD)
    public @interface FieldName {
        String value();
    }

    public static Object parse(String s, Class cls) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        Field fields[] = cls.getDeclaredFields();
        for (Field field : fields) {
//            if (field.getType().equals(Boolean.class))
//            FieldName fieldName = field.getAnnotation(FieldName.class);
//            String name = fieldName.value();
//            JSONObject jsonObject1 = jsonObject.getJSONObject(name);
        }
        return null;
    }
}
