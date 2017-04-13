package com.sumavision.talktv2.http.interceptor;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by sharpay on 16-6-6.
 */
public class UserResponseConverter<T> implements Converter<ResponseBody, T>
{
    private Type type;
    Gson gson = new Gson();

    public UserResponseConverter(Type type)
    {
        this.type = type;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException
    {
        String result = responseBody.string();

        if (result.startsWith("["))
        {
            //return (T) parseUsers(result);
            return null;
        } else
        {
           // return (T) parseUser(result);
            return null;
        }
    }

   /* private User parseUser(String result)
    {
        JSONObject jsonObject = null;
        try
        {
            jsonObject = new JSONObject(result);
            User u = new User();
            u.setUsername(jsonObject.getString("username"));
            return u;
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private List<User> parseUsers(String result)
    {
        List<User> users = new ArrayList<>();
        try
        {
            JSONArray jsonArray = new JSONArray(result);
            User u = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                u = new User();
                u.setUsername(jsonObject.getString("username"));
                users.add(u);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return users;
    }*/
}