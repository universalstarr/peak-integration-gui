package com.peakintegration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Spectrum {
    double[] xs;
    double[] ys;

    long seconds;
    String name;

    public Spectrum(String filename){
        this.name = filename;
        loadFromFile(filename);
    }

    private void loadFromFile(String filename)  {
        try {
            Scanner scanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
            String line;
            String date = null;
            String time = null;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (date == null && line.matches("\\d\\d/\\d\\d/\\d\\d")) {
                    date = line;
                }
                if (time == null && line.matches("\\d\\d:\\d\\d:\\d\\d.\\d\\d")) {
                    time = line;
                }
                if (line.equals("#DATA")) break;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss.SS");
            LocalDateTime localDateTime = LocalDateTime.parse(date + " " + time, formatter);
//        System.out.println(localDateTime);
            seconds = localDateTime.toEpochSecond(ZoneOffset.UTC);
//        System.out.println(seconds);
            List<Double> xArr = new ArrayList<>();
            List<Double> yArr = new ArrayList<>();
            while (scanner.hasNext()) {
                xArr.add(scanner.nextDouble());
                yArr.add(scanner.nextDouble());
            }
            xs = new double[xArr.size()];
            ys = new double[yArr.size()];
            for (int i = 0; i < xs.length; i++) {
                xs[i] = xArr.get(i);
                ys[i] = yArr.get(i);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Spectrum spectrum = new Spectrum(PeakIntegration.class.getResource("2.sp").getFile());
        for(int i=0; i<spectrum.xs.length; i++){
            System.out.println(spectrum.xs[i]+" "+spectrum.ys[i]);
        }
    }
}
