package com.sumavision.talktv2.http.interceptor;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by sharpay on 16-6-6.
 */
public class UserRequestBodyConverter<T> implements Converter<T, RequestBody>
{
    private Gson mGson = new Gson();
    @Override
    public RequestBody convert(T value) throws IOException
    {
        String string = mGson.toJson(value);
        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),string);
    }

}
