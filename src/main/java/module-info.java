module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.peakintegration to javafx.fxml;
    exports com.peakintegration;
}