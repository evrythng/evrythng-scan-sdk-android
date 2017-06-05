package com.evrythng.android.sdk.wrapper.client.service.scan;

import com.evrythng.android.sdk.model.Product;
import com.evrythng.android.sdk.model.Thng;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by phillipcui on 5/30/17.
 */

public class ScanResult {

    @Expose
    List<String> redirections;

    @Expose
    Product product;

    @Expose
    Thng thng;

}
