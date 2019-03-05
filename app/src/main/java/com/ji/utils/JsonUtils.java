package com.ji.utils;

import android.annotation.TargetApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    private static String TAG = "JsonUtils";

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldName {
        String value();
    }

    @TargetApi(28)
    public static Object parse(String s, Class<?> cls) {
        LogUtils.v(TAG, "parse cls:" + cls.getName());
        try {
            Object object = cls.newInstance();
            if (LogUtils.isPhone()) {
                JSONObject jsonObject = new JSONObject(s);
                Field fields[] = cls.getDeclaredFields();
                for (Field field : fields) {
                    FieldName annotation = field.getAnnotation(FieldName.class);
                    if (annotation != null) {
                        Type type = field.getType();
                        String value = annotation.value();
                        field.setAccessible(true);
                        if (type.equals(List.class)) {
                            Type genericType = field.getGenericType();
                            Type[] actualTypes = (Type[]) genericType.getClass()
                                    .getDeclaredMethod("getActualTypeArguments")
                                    .invoke(genericType);

                            Class c = Class.forName(actualTypes[0].getTypeName());
                            JSONArray jsonArray = jsonObject.getJSONArray(value);
                            List<Object> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                list.add(parse(jsonArray.getString(i), c));
                            }
                            field.set(object, list);
                        } else if (type.equals(String.class)) {
                            field.set(object, jsonObject.getString(value));
                        } else if (type.equals(boolean.class)) {
                            field.set(object, jsonObject.getBoolean(value));
                        } else if (type.equals(int.class)) {
                            field.set(object, jsonObject.getInt(value));
                        } else if (type.equals(long.class)) {
                            field.set(object, jsonObject.getLong(value));
                        } else if (type.equals(double.class)) {
                            field.set(object, jsonObject.getDouble(value));
                        }
                    }
                }
            } else {
                com.ji.org.json.JSONObject jsonObject = new com.ji.org.json.JSONObject(s);
                Field fields[] = cls.getDeclaredFields();
                for (Field field : fields) {
                    FieldName annotation = field.getAnnotation(FieldName.class);
                    if (annotation != null) {
                        Type type = field.getType();
                        String value = annotation.value();
                        field.setAccessible(true);
                        if (type.equals(List.class)) {
                            Type genericType = field.getGenericType();
                            Type[] actualTypes = (Type[]) genericType.getClass()
                                    .getDeclaredMethod("getActualTypeArguments")
                                    .invoke(genericType);

                            Class c = Class.forName(actualTypes[0].getTypeName());
                            com.ji.org.json.JSONArray jsonArray = jsonObject.getJSONArray(value);
                            List<Object> list = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                list.add(parse(jsonArray.getString(i), c));
                            }
                            field.set(object, list);
                        } else if (type.equals(String.class)) {
                            field.set(object, jsonObject.getString(value));
                        } else if (type.equals(boolean.class)) {
                            field.set(object, jsonObject.getBoolean(value));
                        } else if (type.equals(int.class)) {
                            field.set(object, jsonObject.getInt(value));
                        } else if (type.equals(long.class)) {
                            field.set(object, jsonObject.getLong(value));
                        } else if (type.equals(double.class)) {
                            field.set(object, jsonObject.getDouble(value));
                        }
                    }
                }
            }
            return object;
        } catch (Exception e) {
            LogUtils.e(TAG, "parse", e);
        }
        return null;
    }
}
