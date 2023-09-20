package com.peakintegration;

public class SpectrumIntegrationResult {
    String filename;
    double lower;
    double upper;
    long seconds;
    double area;

    public SpectrumIntegrationResult(Spectrum spectrum, double lower, double upper){
        this.filename = spectrum.name;
        this.lower = lower;
        this.upper = upper;
        this.seconds = spectrum.seconds;
        int count = 0;
        double sum = 0;
        for(int i=0; i<spectrum.xs.length; i++){
            if(spectrum.xs[i]>=lower && spectrum.xs[i]<=upper){
                count++;
                sum+=spectrum.ys[i];
            }
            if(spectrum.xs[i]>upper){
                break;
            }
        }
        this.area = sum/count;
    }

    public String getFilename(){
        return filename;
    }

    public double getLower(){
        return lower;
    }

    public double getUpper(){
        return upper;
    }

    public long getSeconds(){
        return seconds;
    }

    public double getArea() {
        return area;
    }
}
