package org.example.simple;

import java.util.ArrayList;

public class Main {
    static void main() {
        double items1 = 10.0;
        double items2 = 30.0;
        double unit1 = 1.378;
        double unit2 = 2.876;
        double fx1 = 4.25;
        double fx2 = 4.20;

        InvoiceLineSimple l1 = new InvoiceLineSimple(items1, unit1);
        InvoiceLineSimple l2 = new InvoiceLineSimple(items2, unit2);
        l1.calculate(fx1);
        l2.calculate(fx1);
        IO.println("line 1: "+l1);
        IO.println("line 2: "+l2);

        InvoiceSimple i1 = new InvoiceSimple();
        i1.addLine(l1);
        i1.addLine(l2);
        IO.println("invoice 1 before calculation: "+i1); // Incomplete object
        i1.calculate(fx2);
        IO.println("invoice 1 after calculation: "+i1); // Complete object

        // Note the difference in parameters
        InvoiceLineSimple l3 = new InvoiceLineSimple(unit1, items1);
        InvoiceLineSimple l4 = new InvoiceLineSimple(unit2, items2);
        InvoiceSimple i2 = new InvoiceSimple();
        i2.addLine(l3);
        i2.addLine(l4);
        i2.calculate(fx2);
        IO.println("invoice 2 after calculation: "+i2); // Different results
    }
}

class InvoiceLineSimple {
    Double items;
    Double unitPrice;
    Double origTotal;
    Double fxRate;
    Double total;

    InvoiceLineSimple(Double items, Double unitPrice) {
        this.items = items;
        // Note the roundings, they matter when input parameters are flipped
        this.unitPrice = Math.round(unitPrice * 100.0) / 100.0;
        origTotal = Math.round(this.unitPrice * this.items * 100.0) / 100.0;
    }

    void calculate(double fx) {
        this.fxRate = fx;
        this.total = Math.round(origTotal * fxRate * 100) / 100.0;
    }

    public String toString() {
        return "items: " + items + ", unitPrice: " + unitPrice + ", total (orig): " + origTotal + ", fxRate: " + fxRate + ", total (currency): " + total;
    }
}

class InvoiceSimple {
    ArrayList<InvoiceLineSimple> items = new ArrayList<>();
    Double totalItems;
    Double totalOrig;
    Double totalCurrency;

    InvoiceSimple() {}
    void addLine(InvoiceLineSimple line) {
        items.add(line);
    }

    void calculate(double fx) {
        items.forEach(i -> i.calculate(fx));
        totalItems = items.stream().map(i -> i.items).reduce(0.0, Double::sum);
        totalOrig = items.stream().map(i -> i.origTotal).reduce(0.0, Double::sum);
        totalCurrency = items.stream().map(i -> i.total).reduce(0.0, Double::sum);
    }

    public String toString() {
        return "lines:  " + items.size() + ", items: " + totalItems + ", orig: " + totalOrig + ", currency: " + totalCurrency;
    }
}













