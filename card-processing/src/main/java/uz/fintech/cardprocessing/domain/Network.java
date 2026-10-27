package uz.fintech.cardprocessing.domain;

public enum Network {

    UZCARD, HUMO;

    /** Resolves the network from the card BIN (first digits of the PAN). */
    public static Network fromPan(String pan) {
        if (pan.startsWith("8600")) return UZCARD;
        if (pan.startsWith("9860")) return HUMO;
        throw new IllegalArgumentException("Unknown BIN: " + pan.substring(0, 4));
    }
}
