package org.example.Serializer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfitTracker {

    private String supplierWithHighestProfit = null;
    private double highestProfit = Double.NEGATIVE_INFINITY;
    private Map<String, Double> currentValues = new HashMap<>();
    private static final String STATE_FILE_PATH = "profit_tracker_state.txt";

    public ProfitTracker() {
        // Recupera o estado salvo, incluindo a lista de streams processadas
        loadState();
    }

    public String getSupplierWithHighestProfit() {
        return supplierWithHighestProfit;
    }

    public void setSupplierWithHighestProfit(String supplierWithHighestProfit) {
        this.supplierWithHighestProfit = supplierWithHighestProfit;
    }

    public double getHighestProfit() {
        return highestProfit;
    }

    public void setHighestProfit(double highestProfit) {
        this.highestProfit = highestProfit;
    }

    public void verifica() {

        int cont = 0;
        for (Map.Entry<String, Double> entry : currentValues.entrySet()) {
            if (entry.getValue() > highestProfit) {
                highestProfit = entry.getValue();
                supplierWithHighestProfit = entry.getKey();
                cont++;
                sendMessage(
                        "Ver: New highest profit: " + highestProfit + " for supplier: " + supplierWithHighestProfit);
            }

        }
        if (cont < 1) {
            String supplierWithMaxValue = null;
            double maxValue = Double.NEGATIVE_INFINITY;
            int c = 0;
            for (Map.Entry<String, Double> entry : currentValues.entrySet()) {
                if (entry.getValue() > maxValue) {
                    maxValue = entry.getValue();
                    supplierWithMaxValue = entry.getKey();
                    c++;
                }
            }
            if (c > 0) {
                highestProfit = maxValue;
                supplierWithHighestProfit = supplierWithMaxValue;

                sendMessage(
                        "Ver: New highest profit: " + highestProfit + " for supplier: " + supplierWithHighestProfit);
            }
        }

    }

    public boolean processProfit(String supplier, double profit) {
        currentValues.put(supplier, profit);
        verifica();

        if (profit > highestProfit) {
            highestProfit = profit;
            supplierWithHighestProfit = supplier;
            sendMessage("New highest profit: " + highestProfit + " for supplier: " + supplierWithHighestProfit);

            return true;
        }
        saveState();
        return false;
    }

    private void sendMessage(String message) {
        // Lógica para enviar a mensagem (substitua isso com sua implementação real)
        System.out.println("Sending message: " + message);
    }

    private void saveState() {
        try (Writer writer = new FileWriter(STATE_FILE_PATH, StandardCharsets.UTF_8)) {
            // Serializa o estado atual para o arquivo
            writer.write("SupplierWithHighestProfit:" + supplierWithHighestProfit + "\n");
            writer.write("HighestProfit:" + highestProfit + "\n");
            for (Map.Entry<String, Double> entry : currentValues.entrySet()) {
                writer.write("CurrentValue:" + entry.getKey() + ":" + entry.getValue() + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadState() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STATE_FILE_PATH, StandardCharsets.UTF_8))) {
            // Deserializa o estado do arquivo
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("SupplierWithHighestProfit:")) {
                    supplierWithHighestProfit = line.substring("SupplierWithHighestProfit:".length());
                } else if (line.startsWith("HighestProfit:")) {
                    highestProfit = Double.parseDouble(line.substring("HighestProfit:".length()));
                } else if (line.startsWith("CurrentValue:")) {
                    // Deserializa os valores atuais do arquivo
                    String[] parts = line.split(":");
                    if (parts.length == 3) {
                        currentValues.put(parts[1], Double.parseDouble(parts[2]));
                    }
                }

            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
