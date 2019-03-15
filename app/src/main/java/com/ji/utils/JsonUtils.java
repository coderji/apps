package com.ji.utils;

import android.os.Build;

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

    public static Object parse(String string, Class<?> cls) {
        if (LogUtils.isPhone()) {
            return parseOnPhone(string, cls);
        } else {
            return parseOnPC(string, cls);
        }
    }

    private static Object parseOnPhone(String string, Class<?> cls) {
        try {
            Object object = cls.newInstance();
            JSONObject jsonObject = new JSONObject(string);
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

                        Class c;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            c = Class.forName(actualTypes[0].getTypeName());
                        } else {
                            c = Class.forName(actualTypes[0].toString().split(" ")[1]);
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray(value);
                        List<Object> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(parseOnPhone(jsonArray.getString(i), c));
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
                    } else {
                        Class c;
                        if (Build.VERSION.SDK_INT >= 28) {
                            c = Class.forName(type.getTypeName());
                        } else {
                            c = Class.forName(type.toString().split(" ")[1]);
                        }
                        String s = jsonObject.getString(value);
                        if (s != null && !s.equals("null")) {
                            field.set(object, parse(s, c));
                        }
                    }
                }
            }
            return object;
        } catch (Exception e) {
            LogUtils.e(TAG, "parse cls:" + cls + " string:" + string, e);
        }
        return null;
    }

    private static Object parseOnPC(String string, Class<?> cls) {
        try {
            Object object = cls.newInstance();
            com.ji.org.json.JSONObject jsonObject = new com.ji.org.json.JSONObject(string);
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

                        Class c;
                        if (Build.VERSION.SDK_INT >= 28) {
                            c = Class.forName(actualTypes[0].getTypeName());
                        } else {
                            c = Class.forName(actualTypes[0].toString().split(" ")[1]);
                        }
                        com.ji.org.json.JSONArray jsonArray = jsonObject.getJSONArray(value);
                        List<Object> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(parseOnPC(jsonArray.getString(i), c));
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
                    } else {
                        Class c;
                        if (android.os.Build.VERSION.SDK_INT >= 28) {
                            c = Class.forName(type.getTypeName());
                        } else {
                            c = Class.forName(type.toString().split(" ")[1]);
                        }
                        String s = jsonObject.getString(value);
                        if (s != null && !s.equals("null")) {
                            field.set(object, parse(s, c));
                        }
                    }
                }
            }
            return object;
        } catch (Exception e) {
            LogUtils.e(TAG, "parse cls:" + cls + " string:" + string, e);
        }
        return null;
    }
}
