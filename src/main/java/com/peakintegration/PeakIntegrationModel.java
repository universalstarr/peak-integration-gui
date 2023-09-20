package com.peakintegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PeakIntegrationModel extends Observable {
    private Double integrationLeft;
    private Double integrationRight;

    private Spectrum curSpectrum;

    long startingSeconds;

    private List<Spectrum> spectra;
    private List<SpectrumIntegrationResult> integrationResults;

    public PeakIntegrationModel(){
        this.spectra = new ArrayList<>();
        this.integrationResults = new ArrayList<>();
    }

    public Double getIntegrationLeft() {
        return integrationLeft;
    }

    public Double getIntegrationRight() {
        return integrationRight;
    }

    public void setIntegrationLeft(double integrationLeft) {
        this.integrationLeft = integrationLeft;
        super.notifyObservers();
    }

    public void setIntegrationRight(double integrationRight) {
        this.integrationRight = integrationRight;
        super.notifyObservers();
    }

    public void loadCurSpectrum(String filename){
        curSpectrum = new Spectrum(filename);
    }

    public Spectrum getCurSpectrum(){
        return curSpectrum;
    }

    public void loadSpectra(List<String> filenames){
        this.spectra.clear();
        for(String filename: filenames) {
            this.spectra.add(new Spectrum(filename));
        }

    }

    public void integrateAll(){
        this.setChanged();
        this.integrationResults.clear();
        for(Spectrum spectrum: this.spectra) {
            this.integrationResults.add(new SpectrumIntegrationResult(spectrum, integrationLeft, integrationRight));
        }
        this.startingSeconds = this.spectra.get(0).seconds;
        for(SpectrumIntegrationResult result: this.integrationResults){
            result.seconds = result.seconds - startingSeconds;
        }
        this.notifyObservers();
    }

    public List<SpectrumIntegrationResult> getIntegrationResults(){
        return integrationResults;
    }

    public long getStartingSeconds(){
        return startingSeconds;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        PeakIntegrationController controller = (PeakIntegrationController)o;
        controller.setModel(this);
        System.out.println("Added an observer: "+o.getClass().getName());
    }
}
