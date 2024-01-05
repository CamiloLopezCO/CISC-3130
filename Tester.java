package com.mac286.project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class Tester {

    public static void main(String[] args) {

        float Risk[] = {0.5f, 1f, 2f, 5f, 10f};

        for (int r = 0; r < 5; r++) {
            Vector<Trade> Trades = new Vector<Trade>(3000);

            try {
                //Scanner scanner = new Scanner(new File("C:\\Users\\camil\\Pictures\\Camera Roll\\COPY PASTE\\MAC 286\\Data\ETFs.txt\\"));
                Scanner scanner = new Scanner(new File("C:\\Users\\camil\\Pictures\\Camera Roll\\COPY PASTE\\MAC 286\\Data\\Stocks.txt\\"));
                while (scanner.hasNextLine()) {
                    String symbol = scanner.nextLine();
                    symbolTester tester = new symbolTester(symbol, "C:\\Users\\camil\\Pictures\\Camera Roll\\COPY PASTE\\MAC 286\\Data\\", Risk[r]);
                    tester.test();
                    Trades.addAll(tester.getTrades());
                }

                //TODO compute stats and display.
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //compute the average profit per trade:
            double TotalProfit = 0, TotalProfitLong = 0, TotalProfitShort = 0;
            int numWinners = 0, numWinnersLong = 0, numWinnersShort = 0;
            int numLong = 0, numShort = 0;;
            int TotalHoldingPeriod = 0, TotalHoldingPeriodLong = 0, TotalHoldingPeriodShort = 0;
            for(int i= 0; i<Trades.size(); i++) {
                TotalProfit += Trades.elementAt(i).percentPL();
                TotalHoldingPeriod += Trades.elementAt(i).getHoldingPeriod();
                if(Trades.elementAt(i).getDir() == Direction.LONG) {
                    numLong++;
                    TotalProfitLong += Trades.elementAt(i).percentPL();
                    TotalHoldingPeriodLong += Trades.elementAt(i).getHoldingPeriod();
                    if(Trades.elementAt(i).percentPL() >= 0) {
                        numWinners++;
                        numWinnersLong++;
                    }
                }else {//short trade
                    numShort++;
                    TotalProfitShort += Trades.elementAt(i).percentPL();
                    TotalHoldingPeriodShort += Trades.elementAt(i).getHoldingPeriod();
                    if(Trades.elementAt(i).percentPL() >= 0) {
                        numWinners++;
                        numWinnersShort++;
                    }
                }
            }

            double averageProfit = TotalProfit/(double)Trades.size();
            double averageHoldingPeriod = (double)TotalHoldingPeriod/(double)Trades.size();
            double averageHoldingPeriodDay = averageProfit/averageHoldingPeriod;
            double winningPercent = (double)numWinners/(double)Trades.size()*100;
            //Do the same for longs
            double averageProfitLong = TotalProfitLong/(double)numLong;
            double averageHoldingPeriodLong = (double)TotalHoldingPeriodLong/(double)numLong;
            double averageHoldingPeriodDayLong = averageProfitLong/averageHoldingPeriodLong;
            double winningPercentLong = (double)numWinnersLong/(double)numLong*100;

            //Do the same for shorts
            double averageProfitShort = TotalProfitShort/(double)numShort;
            double averageHoldingPeriodShort = (double)TotalHoldingPeriodShort/(double)numShort;
            double averageHoldingPeriodDayShort = averageProfitShort/averageHoldingPeriodShort;
            double winningPercentShort = (double)numWinnersShort/(double)numShort*100;

            //Compute the stats
            System.out.printf("\n \t-----------Risk: " + Risk[r] +"------------\n");
            System.out.println("Number Trades: " + Trades.size() + "\tNumber TradesLong: " + numLong + "\tNumber TradesShort: " + numShort);
            System.out.println("Winning percent: " + String.format("%.3f",winningPercent) + "\tWinning percent long: " + String.format("%.3f",winningPercentLong) + "\tWinning percent short: " + String.format("%.3f",winningPercentShort));
            // Same for average profit
            System.out.println("Average Profit: " + String.format("%.3f",averageProfit) + "\tAverage Profit Long: " + String.format("%.3f",averageProfitLong) + "\tAverage Profit Short: " + String.format("%.3f",averageProfitShort));

            //same for holding period
            System.out.println("Average holding period: " + String.format("%.3f",averageHoldingPeriod) + "\tAverage holding period Long: " + String.format("%.3f",averageHoldingPeriodLong) + "\tAverage holding period Short: " + String.format("%.3f",averageHoldingPeriodShort));

            //same for Average Profit per trade per day (most important of all)
            System.out.println("Average Profit per trade per day: " + String.format("%.3f",averageHoldingPeriodDay) + "\tAverage Profit per trade per day Long: "+ String.format("%.3f",averageHoldingPeriodDayLong) + "\tAverage Profit per trade per day Short: " + String.format("%.3f",averageHoldingPeriodDayShort));

        }
    }
}