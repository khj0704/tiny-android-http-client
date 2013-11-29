package org.android.io.httpclient.request;

/**
 * @author Daniel
 */
public enum HttpMethod {

    GET(true, false, true), POST(true, true, false), PUT(true, true, true), DELETE(true, false, true),;

    private boolean doInput;
    private boolean doOutput;
    private boolean idemponent;

    private HttpMethod(boolean doInput, boolean doOutput, boolean idemponent) {
        this.doInput = doInput;
        this.doOutput = doOutput;
        this.idemponent = idemponent;
    }

    public String getMethodName() {
        return this.toString();
    }

    public boolean isDoInput() {
        return doInput;
    }

    public boolean isDoOutput() {
        return this.doOutput;
    }

    public boolean isIdemponent() {
        return idemponent;
    }

}
