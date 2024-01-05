package com.mac286.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class symbolTester {
    private float riskFactor;
    private String mSymbol;
    private String dataPath;

    private Vector<Bar> mData;
    private Vector<Trade> mTrades;
    private boolean loaded = false;

    public symbolTester(String s, String p, float risk) {
        riskFactor = risk;
        mSymbol = s;
        dataPath = p;
        mData = new Vector<Bar>(3000, 100);
        mTrades = new Vector<Trade>(200, 100);
        loaded = false;
    }

    public Vector<Trade> getTrades() {
        return mTrades;
    }

    public void loadData() {
        //create file name
        String fileName = dataPath + mSymbol + "_Daily.csv";
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while((line = br.readLine()) != null) {
                //create a bar using the constructor that accepts the data as a String
                Bar b = new Bar(line);
                //add the bar to the Vector
                mData.add(b);
            }
            loaded = true;
            br.close();
            fr.close();
        }catch(IOException e) {
            System.out.println("Something is wrong: " + e.getMessage());
            loaded = false;
            return;
        }
    }

    private boolean xDaysLow(int ind, int days) {
        for (int i = ind-1; i > ind-days; i--) {
            if(mData.elementAt(i).getLow() < mData.elementAt(ind).getLow())
                return false;
        }
        return true;
    }
    private boolean xDaysHigh(int ind, int days) {
        for (int i = ind-1; i > ind-days; i--) {
            if(mData.elementAt(i).getHigh() > mData.elementAt(ind).getHigh())
                return false;
        }
        return true;
    }
    void outcomes(Trade T, int ind) {
        for(int i = ind; i < mData.size(); i++) {
            if(T.getDir() == Direction.LONG) {
                if(mData.elementAt(i).getHigh() > T.getTarget()) { //it is a win
                    //consider a gap day
                    if(mData.elementAt(i).getOpen() > T.getTarget()) {
                        //close at open  a gap day
                        T.close(mData.elementAt(i).getDate(), mData.elementAt(i).getOpen(), i-ind);
                        return;
                    }else {
                        //close the trade at target
                        T.close(mData.elementAt(i).getDate(), T.getTarget(), i-ind);
                        return;
                    }
                } else if(mData.elementAt(i).getLow() < T.getStopLoss()) {
                    //check if there is a gap down
                    if(mData.elementAt(i).getOpen() < T.getStopLoss()) {
                        //get out at the open
                        T.close(mData.elementAt(i).getDate(), mData.elementAt(i).getOpen(), i-ind);
                        return;
                    }else {
                        //get out at stoploss
                        T.close(mData.elementAt(i).getDate(), T.getStopLoss(), i-ind);
                        return;
                    }

                }
            }else {// it is a short trade
                if(mData.elementAt(i).getLow() <= T.getTarget()) { //it is a win
                    //consider a gap day
                    if(mData.elementAt(i).getOpen() < T.getTarget()) {
                        //close at open  a gap down day
                        T.close(mData.elementAt(i).getDate(), mData.elementAt(i).getOpen(), i-ind);
                        return;
                    }else {
                        //close the trade at target
                        T.close(mData.elementAt(i).getDate(), T.getTarget(), i-ind);
                        return;
                    }
                } else if(mData.elementAt(i).getHigh() >= T.getStopLoss()) {
                    //check if there is a gap down
                    if(mData.elementAt(i).getOpen() > T.getStopLoss()) {
                        //get out at the open
                        T.close(mData.elementAt(i).getDate(), mData.elementAt(i).getOpen(), i-ind);
                        return;
                    }else {
                        //get out at stoploss
                        T.close(mData.elementAt(i).getDate(), T.getStopLoss(), i-ind);
                        return;
                    }

                }

            }
        }//end of for
        //if we get here the trade is not closed, close it at the close of the last day
        T.close(mData.elementAt(mData.size()-1).getDate(), mData.elementAt(mData.size()-1).getClose(), mData.size()-1-ind);
    }

    public boolean test() {
        if(!loaded) {
            loadData();
            if (!loaded) {
                System.out.println("cannot load data");
                return false;
            }
        }
        //display the first 120 bars
        /*As an example let's test the following pattern

         */

        for(int i = 10; i < mData.size()-1; i++) {
            if(xDaysLow(i, 10)
                    && mData.elementAt(i).getClose() > mData.elementAt(i).getOpen()
                    && (mData.elementAt(i).getOpen() - mData.elementAt(i).getLow()) > 2 * (mData.elementAt(i).getHigh() - mData.elementAt(i).getClose()))
            {

                float entryprice = mData.elementAt(i+1).getOpen();
                float risk = mData.elementAt(i+1).getOpen() - mData.elementAt(i).getLow() + 0.01f;
                float stoploss = mData.elementAt(i).getLow() - 0.01f;
                float target = entryprice + riskFactor * risk;
                Trade T = new Trade();
                T.open(mSymbol, mData.elementAt(i+1).getDate(), entryprice, stoploss, target, Direction.LONG);
                outcomes(T, i+1);
                //add the trade to the Trade vector
                mTrades.add(T);


            } else if(xDaysHigh(i, 10)
                    && mData.elementAt(i).getClose() < mData.elementAt(i).getOpen()
                    && (mData.elementAt(i).getHigh() - mData.elementAt(i).getOpen()) > 2 * (mData.elementAt(i).getClose() - mData.elementAt(i).getLow()))
            {

                float entryprice = mData.elementAt(i+1).getOpen();
                float risk = mData.elementAt(i).getHigh() - mData.elementAt(i+1).getOpen() + 0.01f;
                float stoploss = mData.elementAt(i).getHigh() + 0.01f;
                float target = entryprice - riskFactor * risk;
                Trade T = new Trade();
                T.open(mSymbol, mData.elementAt(i+1).getDate(), entryprice, stoploss, target, Direction.SHORT);
                outcomes(T, i+1);
                //add the trade to the Trade vector
                mTrades.add(T);
            }
        }

        return true;
    }

}