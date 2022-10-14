package com.zingit.user;

public class CartItem {
    private  final Item item;
    int quantity;

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice(){
        return item.getPrice();
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
