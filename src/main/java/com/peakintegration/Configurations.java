package com.peakintegration;

import java.io.*;

public class Configurations implements Serializable {
    String lastFileLocation;

    static Configurations create(){
        try {
            FileInputStream fileinput =
                    new FileInputStream(System.getProperty("user.home")+"/Desktop/peak_integration_config.txt");
            ObjectInputStream in = new ObjectInputStream(fileinput);
            Configurations configurations  = (Configurations) in.readObject();
            in.close();
            fileinput.close();
            return configurations;
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Configurations();
    }
    static void flush(Configurations configurations){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(System.getProperty("user.home")+"/Desktop/peak_integration_config.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(configurations);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
