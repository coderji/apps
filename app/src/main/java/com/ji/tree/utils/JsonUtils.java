package com.ji.tree.utils;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class JsonUtils {
    private static String TAG = "JsonUtils";

    public @interface FieldName {
        String value();
    }

    public static Object parse(String s, Class<?> cls) {
        try {
            Object object = cls.getDeclaredConstructor().newInstance();
            JSONObject jsonObject = new JSONObject(s);
            Field fields[] = cls.getDeclaredFields();
            for (Field field : fields) {
//            if (field.getType().equals(Boolean.class))
//            FieldName fieldName = field.getAnnotation(FieldName.class);
//            String name = fieldName.value();
//            JSONObject jsonObject1 = jsonObject.getJSONObject(name);
            }
            return object;
        } catch (Exception e) {
            LogUtils.e(TAG, "parse", e);
        }
        return null;
    }
}
