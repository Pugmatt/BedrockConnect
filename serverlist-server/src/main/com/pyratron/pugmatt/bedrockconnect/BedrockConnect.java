package main.com.pyratron.pugmatt.bedrockconnect;

import main.com.pyratron.pugmatt.bedrockconnect.utils.PaletteManager;

public class BedrockConnect {


    public static PaletteManager paletteManager;

    public static void main(String[] args) {
        paletteManager =  new PaletteManager();

        Server server = new Server();
    }

}
