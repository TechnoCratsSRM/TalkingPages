package Model;
/*
Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
 */
import android.util.Log;

public class ListItem {
    private String name;
    private String description;
    private String ge;
    private String series;

    public ListItem(String name, String description,String series,String ge)
    {
        this.name=name;
        this.description=description;
        this.series = series;
        this.ge=ge;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGe(){return ge;}

    public String getSeries(){return series;}
}
