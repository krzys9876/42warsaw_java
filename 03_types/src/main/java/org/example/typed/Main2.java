package org.example.typed;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main2 {
    static void main() {
        Items items1 = new Items(10.0);
        Items items2 = new Items(30.0);
        UnitPrice unit1 = new UnitPrice(1.378);
        UnitPrice unit2 = new UnitPrice(2.876);

        var l1 = new InvoiceLineInput(items1, unit1);
        var l2 = new InvoiceLineInput(items2, unit2);
        //var l3 = new InvoiceLineInput(unit1, items1); // this does not compile!
        IO.println(l1);
        IO.println(l2);

        InvoiceInput input = new InvoiceInput();
        input.addInput(l1);
        input.addInput(l2);
        IO.println(input);

        FxRate fxRate = new FxRate(4.20);

        InvoiceCalculated calc = new InvoiceCalculated(input, fxRate);
        IO.println(calc);
    }
}

record Items(double value) {
    TotalOrig withUntPrice(UnitPrice unitPrice) {
        return new TotalOrig(value * unitPrice.value());
    }
}

record UnitPrice(double value) { }

record TotalOrig(double value) {
    TotalCurrency withFxRate(FxRate fxRate) {
        return new TotalCurrency(Math.round(value * fxRate.value() * 100.0) / 100.0);
    }
}

record FxRate(double value) { }

record TotalCurrency(double value) {}

class InvoiceLineInput {
    Items items;
    UnitPrice unitPrice;

    InvoiceLineInput(Items items, UnitPrice unitPrice) {
        this.items = items;
        this.unitPrice = new UnitPrice(Math.round(unitPrice.value() * 100) / 100.0);
    }

    public String toString() {
        return "items: " + items + ", unitPrice: " + unitPrice;
    }
}

class InvoiceLineCalculated {
    InvoiceLineInput input;
    TotalOrig totalOrig;
    TotalCurrency totalCurrency;
    InvoiceLineCalculated(InvoiceLineInput input, FxRate fxRate) {
        this.input = input;
        totalOrig = input.items.withUntPrice(input.unitPrice);
        totalCurrency = totalOrig.withFxRate(fxRate);
    }

    public String toString() {
        return "items: " + input.items + ", unitPrice: " + input.unitPrice + ", totalOrig: " + totalOrig;
    }
}

class InvoiceInput {
    ArrayList<InvoiceLineInput> input = new ArrayList<>();
    Items totalItems;
    InvoiceInput() {
        totalItems = new Items(0.0);
    }

    void addInput(InvoiceLineInput line) {
        input.add(line);
        totalItems = new Items(totalItems.value() + line.items.value());
    }

    public String toString() {
        return "lines: " + input.size() + ", items: " + totalItems;
    }
}

class InvoiceCalculated {
    InvoiceInput input;
    ArrayList<InvoiceLineCalculated> calculated;
    TotalOrig totalOrig;
    TotalCurrency totalCurrency;
    InvoiceCalculated(InvoiceInput input, FxRate fxRate) {
        this.input = input;
        calculated = input.input.stream().map(c -> new InvoiceLineCalculated(c, fxRate)).collect(Collectors.toCollection(ArrayList::new));
        double to = calculated.stream().map(c -> c.totalOrig.value()).reduce(0.0, Double::sum);
        //totalOrig = new TotalOrig(to);
        totalOrig = new TotalOrig(Math.round(to *100.0) / 100.0);
        double tc = calculated.stream().map(c -> c.totalCurrency.value()).reduce(0.0, Double::sum);
        totalCurrency = new TotalCurrency(Math.round(tc *100.0) / 100.0);
    }

    public String toString() {
        return input.toString() + ", totalOrig: " + totalOrig + ", totalCurrency: " + totalCurrency;
    }
}