package com.example.davide.iumex3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by davide on 17/10/14.
 */
public class LineGraph extends View {

    // la serie da visualizzare
    protected Series<?> series;

    // indice selezionato nella serie
    private int selectedIndex;
    // valore massimo nella serie
    private double max = Double.MIN_VALUE;
    // valore minimo nella serie
    private double min = Double.MAX_VALUE;

    //region variabili controllo disegno del grafico

    // spazio tra assi e bordo del controllo
    private int axisDelta = 120;
    // distanza fra due tick su un asse
    private int tickDistance = 75;
    // lunghezza di un tick
    private int tickLength = 15;
    // distanza fra i valori numerici e gli assi
    private int labelPadding = 20;
    // area di tocco attorno ad un punto della serie
    private int precision = 20;
    // colore di sfondo
    private int backgroundColor = Color.WHITE;
    // colore degli assi
    private int axisColor = Color.BLACK;
    // larghezza della linea degli assi
    private int axisLineWidth = 3;
    // colore della linea della serie
    private int seriesColor = Color.RED;
    // larghezza della linea della serie
    private int seriesWidth = 6;
    // flag per il disegno delle labels
    private boolean drawLabels = true;
    // font per il disegno delle etichette
    private Typeface font = Typeface.create("Helvetica", Typeface.NORMAL);
    // dimensione del testo per le etichette
    private float textSize = 24;
    //endregion

    //region variabili che controllano il viewport
    // fattore di scala
    private float zoom = 1.0f;
    // punto in alto a sinistra del rettangolo del viewport
    private Point translate = new Point(0,0);
    //endregion



    public LineGraph(Context context) {
        super(context);
    }

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //region stato interno
    public Series<?> getSeries(){
        return series;
    }

    public void setSeries(Series<?> s){
        this.series = s;
        this.selectedIndex = -1;
        updateRange();
        updateGraph();
    }

    public void setSelectedIndex(int sel){
        this.selectedIndex = sel;
    }

    public int getBackgroundColor(){
        return backgroundColor;
    }

    public void setBackgroundColor(int color){
        this.backgroundColor = color;
    }

    public int getAxisColor(){
        return axisColor;
    }

    public void setAxisColor(int color){
        this.axisColor = color;
    }

    public void setSeriesColor(int color){
        this.seriesColor = color;
    }

    public int getSeriesColor(){
        return this.seriesColor;
    }

    public void setSeriesWidth(int width){
        this.seriesWidth = width;
    }

    public int getSeriesWidth() {
        return seriesColor;
    }

    public int getAxisDelta() {
        return axisDelta;
    }

    public void setAxisDelta(int axisDelta) {
        this.axisDelta = axisDelta;
    }

    public int getAxisLineWidth(){
        return axisLineWidth;
    }

    public void setAxisLineWidth(int width){
        this.axisLineWidth = width;
    }

    public int getTickDistance() {
        return tickDistance;
    }

    public void setTickDistance(int tickDistance) {
        this.tickDistance = tickDistance;
    }

    public int getTickLength() {
        return tickLength;
    }

    public void setTickLength(int tickLength) {
        this.tickLength = tickLength;
    }

    public int getLabelPadding() {
        return labelPadding;
    }

    public void setLabelPadding(int labelPadding) {
        this.labelPadding = labelPadding;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public Point getTranslate() {
        return translate;
    }

    public void setTranslate(Point translate) {
        this.translate = translate;
    }

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    public Typeface getFont() {
        return font;
    }

    public void setFont(Typeface font) {
        this.font = font;
    }

    public float getTextSize(){
        return textSize;
    }

    public void setTextSize(float size){
        this.textSize = size;
    }
    //endregion

    //region disegno
    @Override
    protected  void onDraw(Canvas canvas){

        // utilizziamo questo oggetto per definire
        // colori, font, tipi di linee ecc
        Paint paint = new Paint();

        // impostiamo l'antialiasing
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        // cancelliamo lo sfondo
        paint.setColor(this.backgroundColor);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        // dimensioni del grafico
        float width = canvas.getWidth() - 2 * axisDelta;
        float height = canvas.getHeight() - 2 * axisDelta;

        // salviamo la configurazione attuale del canvas, in modo da poterla ripristinare
        // per configurazione si intende lo stato delle trasformazioni (scalatura e traslazione)
        canvas.save();

        // impostiamo la scalatura
        canvas.scale(this.zoom, this.zoom);

        //impostiamo la traslazione del viewport
        canvas.translate(translate.x, translate.y);

        // impostiamo una ulteriore traslazione per disegnare gli assi staccati dal bordo
        canvas.save();
        canvas.translate(axisDelta, axisDelta);

        // disegniamo gli assi
        paint.setColor(this.axisColor);
        paint.setStrokeWidth(this.axisLineWidth);
        canvas.drawLine(0,0,0,height, paint);
        canvas.drawLine(0, height, width, height, paint);

        // disegniamo la linea
        if(this.series != null){
            paint.setColor(this.seriesColor);
            paint.setStrokeWidth(this.seriesWidth);

            // calcoliamo i pixel da utilizzare per unita' sull'asse x
            double pxpu = 1.0 * width / (this.series.getCount() - 1);
            // calcoliamo i pixel per unita'asse y
            double pypu = height / (max - min);

            double d1, d2, val1, val2 = 0;
            // il valore minimo della serie sara' lo 0 sulle y
            for(int i = 1; i < this.series.getCount(); i++){
                d1 = this.series.valueAt(i - 1);
                d2 = this.series.valueAt(i);

                val1 = (d1 - min) * pypu;
                val2 = (d2 - min) * pypu;

                canvas.drawLine(
                        (float) ((i-1) * pxpu),
                        (float) (height - val1),
                        (float) (i * pxpu),
                        (float) (height - val2),
                        paint);
            }

            // disegno un rettangolo attorno al punto selezionato
            if(this.selectedIndex != -1){
                double d = this.series.valueAt(this.selectedIndex);
                double val = (d - min) * pypu;

                canvas.drawRect(
                        (float)(pxpu * selectedIndex - precision/2),
                        (float)(height - val - precision / 2),
                        (float)(pxpu * selectedIndex - precision/2) + precision,
                        (float)(height - val - precision / 2) + precision, paint);
            }

            // disegno le etichette testuali
            if(this.isDrawLabels()){

                // --------------------------------
                // disegniamo le tacche sull'asse y
                //---------------------------------


                // quanti tick dobbiamo disegnare?
                int nTick = (int) (height / tickDistance);

                paint.setColor(this.axisColor);
                paint.setStrokeWidth(this.axisLineWidth);
                paint.setTypeface(this.font);
                paint.setTextSize(this.textSize);

                // stabiliamo quante cifre di un numero possiamo scrivere
                // misurando quanti caratteri 0 ci stanno tra una tacca ed
                // un'altra

                float zeroWidth = paint.measureText("0");
                // lo spazio disponibile tra il bordo del controllo e l'asse
                int availableSpace = axisDelta - 2 * labelPadding;

                // il numero di zeri che ci stanno nello spazio disponibile
                int zeroes = (int)((axisDelta - 2 * labelPadding)/ zeroWidth);

                // valore (non in pixel ma numerico) della distanza fra due tacche
                double tickValue = 1.0 * (max-min) / nTick;

                for(int i = 0; i <= nTick; i++){
                    // calcoliamo il valore associato ad ogni tacca
                    // il numero di tacche puo' essere impostato dallo sviluppatore
                    String toDraw = "" + (min + i * tickValue);

                    if(paint.measureText(toDraw) > availableSpace){
                        // devo tagliare la stringa
                        toDraw = toDraw.substring(0, zeroes);
                    }

                    // disegno la tacchetta
                    canvas.drawLine(
                            -tickLength,
                            height - (i* tickDistance) - paint.getFontMetrics().bottom,
                            0,
                            height - (i* tickDistance) - paint.getFontMetrics().bottom,
                            paint);

                    // disegno il valore numerico
                    canvas.drawText(
                            toDraw,
                            -axisDelta + labelPadding,
                            height - (i* tickDistance),
                            paint);
                }

                // --------------------------------
                // disegniamo le tacche sull'asse x
                //---------------------------------

                // tacchette
                for(int i = 0; i < this.series.getCount(); i++){
                    canvas.drawLine(
                            Math.round(i * pxpu),
                            height + tickLength,
                            Math.round(i * pxpu),
                            height,
                            paint);
                }

                // disegnamo le label in basso:
                // calcoliamo il numero di lettere che possiamo usare
                // in larghezza, in altezza siamo sicuri che ci stiamo

                // limite massimo di pixel per lettera
                // la M e' il carattere piu' largo
                float pixelPerM = paint.measureText("M");

                // quante lettere per ogni label ?
                int chars = (int) (pxpu / pixelPerM);

                if(chars > 3){
                    // facciamo apparire le label quando ci stanno almeno 4
                    // caratteri, se non ci stanno tutte mettiamo i puntini
                    for(int i = 0; i < this.series.getCount(); i++){
                        String toDraw = this.series.itemAt(i).toString();
                        if(paint.measureText(toDraw) > pxpu){
                            // devo tagliare la stringa
                            toDraw = toDraw.substring(0, chars - 3) + "...";
                        }

                        float actualStringWidth = paint.measureText(toDraw);
                        canvas.drawText(
                                toDraw,
                                Math.round(i * pxpu) - actualStringWidth / 2,
                                height + axisDelta/ 2,
                                paint);
                    }

                }

            }
        }

        // si ritorna alla trasformazione del viewport
        canvas.restore();

        //si ritorna alla trasformazione iniziale
        canvas.restore();
    }




    //endregion

    private void updateGraph() {

        this.invalidate();
    }

    private void updateRange() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        for (double d : this.series.getValues()) {
            if (this.min > d) {
                this.min = d;
            }

            if (this.max < d) {
                this.max = d;
            }
        }
    }



}
