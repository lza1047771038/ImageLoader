package com.lza.imageloader.load;

import com.lza.imageloader.interfaces.Requestable;

public class RequestBuilder {

    public static synchronized Requestable createSingleRequest() {
        return new SingleRequest();
    }
}
