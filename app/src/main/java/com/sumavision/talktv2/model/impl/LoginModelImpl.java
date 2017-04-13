package com.sumavision.talktv2.model.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.LoginModel;
import com.sumavision.talktv2.model.entity.UserInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import dac3rdparty.org.apache.commons.codec.binary.Base64;

/**
 * Created by sharpay on 16-8-23.
 */
public class LoginModelImpl implements LoginModel {


    @Override
    public void release() {

    }
}
